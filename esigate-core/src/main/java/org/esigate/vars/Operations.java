/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.esigate.vars;

import java.util.ArrayList;

/**
 * Support for ESI expressions.
 * 
 * @author Alexis Thaveau
 * @author Nicolas Richeton
 * 
 */
public class Operations {

	private static boolean executeOperation(String op) {

		int i;
		Double dop1, dop2;
		String op1, op2;

		try {
			if (op.indexOf("==") != -1) {
				i = op.indexOf("==");
				op1 = VarUtils.removeSimpleQuotes(op.substring(0, i));
				op2 = VarUtils.removeSimpleQuotes(op.substring(i + 2));

				dop1 = getOperandAsNumeric(op1);
				dop2 = getOperandAsNumeric(op2);
				if (dop1 != null && dop2 != null) {
					return dop1.equals(dop2);
				}

				return op1.equals(op2);

			} else if (op.indexOf("!=") != -1) {
				i = op.indexOf("!=");
				op1 = VarUtils.removeSimpleQuotes(op.substring(0, i));
				op2 = VarUtils.removeSimpleQuotes(op.substring(i + 2));

				dop1 = getOperandAsNumeric(op1);
				dop2 = getOperandAsNumeric(op2);
				if (dop1 != null && dop2 != null) {
					return !dop1.equals(dop2);
				}

				return !op1.equals(op2);

			} else if (op.indexOf(">=") != -1) {
				i = op.indexOf(">=");
				op1 = VarUtils.removeSimpleQuotes(op.substring(0, i));
				op2 = VarUtils.removeSimpleQuotes(op.substring(i + 2));

				dop1 = getOperandAsNumeric(op1);
				dop2 = getOperandAsNumeric(op2);
				if (dop1 != null && dop2 != null) {
					return dop1.doubleValue() >= dop2.doubleValue();
				}
				int cmp = op1.compareTo(op2);
				return cmp >= 0;

			} else if (op.indexOf("<=") != -1) {
				i = op.indexOf("<=");
				op1 = VarUtils.removeSimpleQuotes(op.substring(0, i));
				op2 = VarUtils.removeSimpleQuotes(op.substring(i + 2));

				dop1 = getOperandAsNumeric(op1);
				dop2 = getOperandAsNumeric(op2);
				if (dop1 != null && dop2 != null) {
					return dop1.doubleValue() <= dop2.doubleValue();
				}

				int cmp = op1.compareTo(op2);
				return cmp <= 0;

			} else if (op.indexOf(">") != -1) {
				i = op.indexOf(">");
				op1 = VarUtils.removeSimpleQuotes(op.substring(0, i));
				op2 = VarUtils.removeSimpleQuotes(op.substring(i + 1));

				dop1 = getOperandAsNumeric(op1);
				dop2 = getOperandAsNumeric(op2);
				if (dop1 != null && dop2 != null) {
					return dop1.doubleValue() > dop2.doubleValue();
				}

				int cmp = op1.compareTo(op2);
				return cmp > 0;
			} else if (op.indexOf("<") != -1) {
				i = op.indexOf("<");
				op1 = VarUtils.removeSimpleQuotes(op.substring(0, i));
				op2 = VarUtils.removeSimpleQuotes(op.substring(i + 1));

				dop1 = getOperandAsNumeric(op1);
				dop2 = getOperandAsNumeric(op2);
				if (dop1 != null && dop2 != null) {
					return dop1.doubleValue() < dop2.doubleValue();
				}

				int cmp = op1.compareTo(op2);
				return cmp < 0;
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	private static boolean executeOperations(ArrayList<String> operands, ArrayList<String> operations) {
		boolean res = false;
		ArrayList<Boolean> results = new ArrayList<Boolean>();

		try {
			for (String op : operands) {
				results.add(executeOperation(op));
			}

			if (results.size() == 1) {
				if (operations.size() == 1 && operations.get(0).equals("!")) {
					return !results.get(0);
				} else {
					return results.get(0);
				}
			}

			int i = 1;
			res = results.get(0);
			for (String op : operations) {
				if (op.equals("&")) {
					res = res && results.get(i);
				} else if (op.equals("|")) {
					res = res || results.get(i);
				} else {
					res = false;
				}
				i++;
			}
		} catch (Exception e) {
			return false;
		}

		return res;
	}

	/**
	 * Get an operand as a numeric type.
	 * 
	 * @param op
	 *            operand as String
	 * @return Double value or null if op is not numeric
	 */
	private static Double getOperandAsNumeric(String op) {
		Double d = null;
		try {
			d = Double.valueOf(op);
		} catch (Exception e) {
			// Null is returned if not numeric.
		}
		return d;
	}

	public static boolean processOperators(String test) {

		if (test == null || test.equals("")) {
			return false;
		}

		ArrayList<String> operands = new ArrayList<String>();
		ArrayList<String> operations = new ArrayList<String>();

		String s = test.replaceAll(" ", "");
		if (s.startsWith("!")) {
			operations.add("!");
		}
		if (s.indexOf('(') == -1) {
			s = "(" + s + ")";
		}

		// allocate (...)
		try {
			while (s.length() > 0) {
				int sbIndex = s.indexOf(')');
				operands.add(s.substring(s.indexOf('(') + 1, sbIndex));
				if (s.length() > sbIndex + 1) {
					String oper = s.substring(sbIndex + 1, s.substring(sbIndex).indexOf('(') + sbIndex);
					operations.add(oper);
					s = s.substring(sbIndex + 2);
				} else {
					s = "";
				}

			}
		} catch (Exception e) {
			return false;
		}

		return executeOperations(operands, operations);
	}

}
