package org.esigate.vars;

import java.util.ArrayList;

public class Operations {

	private static boolean executeOperation(String op) {

		int i;
		double dop1, dop2;
		String op1, op2;

		try {
			if (op.indexOf("==") != -1) {
				i = op.indexOf("==");
				op1 = op.substring(0, i);
				op2 = op.substring(i + 2);
				if (op.indexOf("'") != -1) {
					return op1.equals(op2);
				} else {
					dop1 = Double.parseDouble(op1);
					dop2 = Double.parseDouble(op2);
					return dop1 == dop2;
				}
			} else if (op.indexOf("!=") != -1) {
				i = op.indexOf("!=");
				op1 = op.substring(0, i);
				op2 = op.substring(i + 2);
				if (op.indexOf("'") != -1) {
					return !op1.equals(op2);
				} else {
					dop1 = Double.parseDouble(op1);
					dop2 = Double.parseDouble(op2);
					return dop1 != dop2;
				}
			} else if (op.indexOf(">=") != -1) {
				i = op.indexOf(">=");
				op1 = op.substring(0, i);
				op2 = op.substring(i + 2);
				if (op.indexOf("'") != -1) {
					int cmp = op1.compareTo(op2);
					return cmp >= 0;
				} else {
					dop1 = Double.parseDouble(op1);
					dop2 = Double.parseDouble(op2);
					return dop1 >= dop2;
				}
			} else if (op.indexOf("<=") != -1) {
				i = op.indexOf("<=");
				op1 = op.substring(0, i);
				op2 = op.substring(i + 2);
				if (op.indexOf("'") != -1) {
					int cmp = op1.compareTo(op2);
					return cmp <= 0;
				} else {
					dop1 = Double.parseDouble(op1);
					dop2 = Double.parseDouble(op2);
					return dop1 <= dop2;
				}
			} else if (op.indexOf(">") != -1) {
				i = op.indexOf(">");
				op1 = op.substring(0, i);
				op2 = op.substring(i + 1);
				if (op.indexOf("'") != -1) {
					int cmp = op1.compareTo(op2);
					return cmp > 0;
				} else {
					dop1 = Double.parseDouble(op1);
					dop2 = Double.parseDouble(op2);
					return dop1 > dop2;
				}
			} else if (op.indexOf("<") != -1) {
				i = op.indexOf("<");
				op1 = op.substring(0, i);
				op2 = op.substring(i + 1);
				if (op.indexOf("'") != -1) {
					int cmp = op1.compareTo(op2);
					return cmp < 0;
				} else {
					dop1 = Double.parseDouble(op1);
					dop2 = Double.parseDouble(op2);
					return dop1 < dop2;
				}
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	private static boolean executeOperations(ArrayList<String> operands,
			ArrayList<String> operations) {
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
					String oper = s.substring(sbIndex + 1, s.substring(sbIndex)
							.indexOf('(')
							+ sbIndex);
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
