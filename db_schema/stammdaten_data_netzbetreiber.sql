\set ON_ERROR_STOP on

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.1
-- Dumped by pg_dump version 9.5.0

-- Started on 2016-03-31 11:38:13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = stammdaten, pg_catalog;

--
-- TOC entry 4703 (class 0 OID 535781)
-- Dependencies: 269
-- Data for Name: netz_betreiber; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY netz_betreiber (id, netzbetreiber, idf_netzbetreiber, is_bmn, mailverteiler, aktiv) FROM stdin;
X	ausländ. Messnetze	\N	t	X	t
PA	Park	R	t	PA	t
A	BfS (Spuren)	A	t	A	t
B	vTI	\N	t	B	t
D	BfS/ZdB	D	t	D	t
E	BfE	\N	t	E	t
F	FhG	\N	t	F	f
G	BfG	\N	t	G	t
H	BSH	\N	t	H	t
I	BfS (LSt. AB)	\N	t	I	t
L	BfS (LSt. TW ...)	\N	t	L	t
M	BMUB	\N	t	M	t
N	MRI	\N	t	N	t
S	Sonstige	\N	t	S	t
T	PTB	\N	t	T	t
U	BfS (ABI)	U	t	U	t
W	DWD	W	t	W	t
Z	BfS (ODL)	Z	t	Z	t
01	Schleswig-Holstein	a	f	01	t
02	Hamburg	b	f	02	t
03	Niedersachsen	c	f	03	t
04	Bremen	d	f	04	t
05	Nordrhein-Westfalen	e	f	05	t
06	Hessen	f	f	06	t
07	Rheinland-Pfalz	g	f	07	t
08	Baden-Württemberg	h	f	08	t
09	Bayern	i	f	09	t
10	Saarland	j	f	10	t
11	Berlin	k	f	11	t
12	Brandenburg	l	f	12	t
13	Mecklenburg-Vorpommern	m	f	13	t
14	Sachsen	n	f	14	t
15	Sachsen-Anhalt	o	f	15	t
16	Thüringen	p	f	16	t
18	Endlager (Bundesaufsicht)	r	f	18	t
19	ZdB-Testnetz	s	f	19	t
17	Bundeswehr	q	f	17	t
\.
