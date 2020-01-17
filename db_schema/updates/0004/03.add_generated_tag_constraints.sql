ALTER TABLE stamm.tag ADD COLUMN generated boolean;
ALTER TABLE stamm.tag ADD UNIQUE(tag, mst_id);
CREATE UNIQUE INDEX gen_tag_unique_idx ON stamm.tag (tag) WHERE generated = true;