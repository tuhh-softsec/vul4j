--
-- PostgreSQL database dump
--

-- Dumped from database version 10.6
-- Dumped by pg_dump version 10.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: status_reihenfolge; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY stamm.status_reihenfolge (id, von_id, zu_id) FROM stdin;
2	1	3
3	1	4
4	1	5
5	1	14
7	2	3
8	2	4
9	2	5
10	2	6
11	2	7
12	2	8
13	2	9
14	2	14
15	3	2
17	3	4
18	3	5
22	3	9
23	3	14
24	4	2
25	4	3
27	4	5
31	4	9
32	4	14
33	5	2
34	5	3
35	5	4
40	5	9
41	5	14
43	6	7
44	6	8
45	6	9
46	6	10
47	6	11
48	6	12
49	6	13
50	6	15
51	7	6
53	7	8
54	7	9
58	7	13
59	7	15
60	8	6
61	8	7
63	8	9
67	8	13
68	8	15
69	9	2
70	9	3
71	9	4
72	9	6
73	9	7
74	9	8
76	9	15
78	10	11
79	10	12
80	10	13
81	10	16
82	11	10
84	11	12
85	11	13
86	11	16
87	12	10
88	12	11
90	12	13
91	12	16
92	13	2
93	13	3
94	13	4
95	13	10
96	13	11
97	13	12
99	13	16
100	14	2
101	14	3
102	14	4
103	14	5
105	15	1
106	15	2
107	15	3
108	15	4
109	15	5
110	15	6
111	15	7
112	15	8
113	15	9
114	15	14
116	16	6
117	16	7
118	16	8
119	16	9
120	16	10
121	16	11
122	16	12
123	16	13
124	16	15
127	1	7
126	1	6
1	1	2
\.


--
-- PostgreSQL database dump complete
--

