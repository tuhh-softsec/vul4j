import csv
import json
import urllib.request
from unidiff import PatchSet

class PatchMiner:

    def __init__(self):
        self.DATASET_FILE = 'vulas_db_msr2019_release.csv'
        self.MINED_PATCH_FILE = 'mined_patches.csv'

        # [{patch_id, patch_diff_url},...]
        self.patch_db = []

        self.load_dataset()
        self.mine_patch_diffs()

    def mine_patch_diffs(self):
        data_file = open(self.MINED_PATCH_FILE, 'a')
        no = 0
        for patch in self.patch_db:
            print("-------- " + patch['vul_id'])
            no += 1
            patch_diff = self.parse_diff(patch['commit_url'] + '.diff', True)
            if patch_diff is not None:
                patch['change_file'] = len(patch_diff)
                patch['change_line'] = self.count_change_lines(patch_diff)

                total_added_lines = 0
                total_removed_lines = 0
                for key in patch_diff:
                    total_added_lines += patch_diff[key]['added_line_count']
                    total_removed_lines += patch_diff[key]['removed_line_count']

                data_file.write(str(patch['vul_id']) + ','
                                + str(patch['change_file']) + ',' + str(patch['change_line']) + ','
                                + str(total_added_lines) + ',' + str(total_removed_lines) + '\n')

                data_file.flush()
                if len(patch_diff) == 0:
                    print('Patch with zero-related to Java source code')
                print(patch)
        data_file.close()

    def load_dataset(self):
        with open(self.DATASET_FILE, 'r') as file:
            csv_reader = csv.reader(file, delimiter=',')
            next(csv_reader)  # skip header
            for row in csv_reader:
                self.patch_db.append({
                    'vul_id': row[0],
                    'commit_hash': row[2],
                    'commit_url': row[1] + '/commit/' + row[2]})

    @staticmethod
    def is_java_test_file(file_path):
        if file_path.endswith('.java'):
            if file_path.endswith('Test.java') or file_path.endswith('Tests.java') \
                    or file_path.endswith('TestCase.java') or file_path.endswith('TestCases.java') \
                    or '/test/' in file_path:
                return True
            else:
                path_parts = file_path.split('/')
                if path_parts[-1].startswith('Test'):
                    return True
        return False

    def is_java_source_file(self, file_path):
        return file_path.endswith('.java') and not self.is_java_test_file(file_path)

    @staticmethod
    def count_change_lines(patch_diff):
        count = 0
        for key in patch_diff:
            count += len(patch_diff[key]['change_lines'])
        return count

    def parse_diff(self, diff_url, allow_test_change=True):
        try:
            diff = urllib.request.urlopen(diff_url)
            patch = PatchSet(diff, encoding=diff.headers.get_content_charset())
        except Exception as e:
            print('Error for: ' + diff_url)
            print(e)
            return None

        if not allow_test_change:
            for a_file in patch.added_files:
                file_path = a_file.path
                if self.is_java_test_file(file_path):
                    print("Found test file in commit: " + file_path)
                    return None

            for r_file in patch.removed_files:
                file_path = r_file.path
                if self.is_java_test_file(file_path):
                    print("Found test file in commit: " + file_path)
                    return None

        change_data = {}
        for m_file in patch.modified_files:
            file_path = m_file.path
            if not allow_test_change and self.is_java_test_file(file_path):
                print("Found test file in commit: " + file_path)
                return None

            if self.is_java_source_file(file_path):
                change_lines = []
                added_line_count = 0
                removed_line_count = 0
                for hunk in m_file:
                    filtered_list = list(filter(lambda l: l.line_type == '+', hunk))
                    addition = len(filtered_list)

                    for i in range(0, len(hunk)):
                        line = hunk[i]
                        if line.line_type == '-' and addition <= 0:
                            change_lines.append(line.source_line_no)
                        elif line.line_type == '+':
                            change_lines.append(line.target_line_no)

                        if line.line_type == '+':
                            added_line_count += 1
                        elif line.line_type == '-':
                            removed_line_count += 1

                change_data[file_path] = {}
                change_data[file_path]['change_lines'] = change_lines
                change_data[file_path]['added_line_count'] = added_line_count
                change_data[file_path]['removed_line_count'] = removed_line_count

        self.parse_simple_diff(patch.added_files, change_data, forward=True)
        self.parse_simple_diff(patch.removed_files, change_data, forward=False)

        return change_data

    def parse_simple_diff(self, change_files, change_data, forward=True):
        for file in change_files:
            file_path = file.path
            if self.is_java_source_file(file_path):
                change_lines = set()
                added_line_count = 0
                removed_line_count = 0
                for hunk in file:
                    for line in hunk:
                        cline = line.target_line_no if forward else line.source_line_no
                        if cline:
                            change_lines.add(cline)

                            if line.line_type == '+':
                                added_line_count += 1
                            elif line.line_type == '-':
                                removed_line_count += 1

                change_data[file_path] = {}
                change_data[file_path]['change_lines'] = change_lines
                change_data[file_path]['added_line_count'] = added_line_count
                change_data[file_path]['removed_line_count'] = removed_line_count


if __name__ == '__main__':
    miner = PatchMiner()
