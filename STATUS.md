## Reproduction status of Vul4J

### How to verify the reproducibility of the vulnerabilities
Please run the below command:
```
vul4j verify --id VUL4J-1 VUL4J-2 VUL4J-3 VUL4J-4 VUL4J-5 VUL4J-6 VUL4J-7 VUL4J-8 VUL4J-9 VUL4J-10 VUL4J-11 VUL4J-12 VUL4J-13 VUL4J-14 VUL4J-15 VUL4J-16 VUL4J-17 VUL4J-18 VUL4J-19 VUL4J-20 VUL4J-21 VUL4J-22 VUL4J-23 VUL4J-24 VUL4J-25 VUL4J-26 VUL4J-27 VUL4J-28 VUL4J-29 VUL4J-30 VUL4J-31 VUL4J-32 VUL4J-33 VUL4J-34 VUL4J-35 VUL4J-36 VUL4J-37 VUL4J-38 VUL4J-39 VUL4J-40 VUL4J-41 VUL4J-42 VUL4J-43 VUL4J-44 VUL4J-45 VUL4J-46 VUL4J-47 VUL4J-48 VUL4J-49 VUL4J-50 VUL4J-51 VUL4J-52 VUL4J-53 VUL4J-54 VUL4J-55 VUL4J-56 VUL4J-57 VUL4J-58 VUL4J-59 VUL4J-60 VUL4J-61 VUL4J-62 VUL4J-63 VUL4J-64 VUL4J-65 VUL4J-66 VUL4J-67 VUL4J-68 VUL4J-69 VUL4J-70 VUL4J-71 VUL4J-72 VUL4J-73 VUL4J-74 VUL4J-75 VUL4J-76 VUL4J-77 VUL4J-78 VUL4J-79
```

### Reproduction results
* Reproduction logs by 28.11.2023: [reproduction.txt](reproduction/reproduction.txt), reproducible vulnerabilities: [successful_vulns.txt](reproduction/successful_vulns.txt).

* (\*) Some project versions are not *fully compilable*, however, the modules that contain the target vulnerabilities and their PoVs are compilable. Therefore, the vulnerabilities are *reproducible*. These cases are indicated with a yellow circle (🟡).

* Some projects needed manual modifications in order to be reproducible (dependency changes, other fixes from later commits). The vul4j script cannot automatically replace or change all of these files, so manual intervention is needed. These cases are indicated with a hand (🖐).

| Vuln ID   | Compilable | PoV(s) | Reproducible |
|-----------|:------:|:------:|:-------:|
| VUL4J-1	|	✅	|	✅	| 	✅	|
| VUL4J-2	|	✅	|	✅	| 	✅	|
| VUL4J-3	|	✅	|	✅	| 	✅	|
| VUL4J-4	|	✅	|	✅	| 	✅	|
| VUL4J-5	|	✅	|	✅	| 	✅	|
| VUL4J-6	|	✅	|	✅	| 	✅	|
| VUL4J-7	|	✅	|	✅	| 	✅	|
| VUL4J-8	|	✅	|	✅	| 	✅	|
| VUL4J-9	|	✅	|	✅	| 	✅	|
| VUL4J-10	|	✅	|	✅	| 	✅	|
| VUL4J-11	|	✅	|	✅	| 	✅	|
| VUL4J-12	|	✅	|	✅	| 	✅	|
| VUL4J-13	|	✅	|	✅	| 	✅	|
| VUL4J-14	|	✅	|	✅	| 	✅	|
| VUL4J-15	|	❌	|	✅	| 	🟡	 |
| VUL4J-16	|	✅	|	✅	| 	✅	|
| VUL4J-17	|	✅	|	✅	| 	✅	|
| VUL4J-18	|	✅	|	✅	| 	✅	|
| VUL4J-19	|	✅	|	✅	| 	✅	|
| VUL4J-20	|	✅	|	✅	| 	✅	|
| VUL4J-21	|	✅	|	✅	| 	✅	|
| VUL4J-22	|	✅	|	✅	| 	✅	|
| VUL4J-23	|	🖐 	 |	 ✅	 | 	 🖐	  |
| VUL4J-24	|	✅	|	✅	| 	✅	|
| VUL4J-25	|	✅	|	✅	| 	✅	|
| VUL4J-26	|	✅	|	✅	| 	✅	|
| VUL4J-27	|	✅	|	✅	| 	✅	|
| VUL4J-28	|	✅	|	✅	| 	✅	|
| VUL4J-29	|	✅	|	✅	| 	✅	|
| VUL4J-30	|	✅	|	✅	| 	✅	|
| VUL4J-31	|	✅	|	✅	| 	✅	|
| VUL4J-32	|	✅	|	✅	| 	✅	|
| VUL4J-33	|	✅	|	✅	| 	✅	|
| VUL4J-34	|	✅	|	✅	| 	✅	|
| VUL4J-35	|	🖐   |	 ✅	 | 	 🖐	  |
| VUL4J-36	|	✅	|	✅	| 	✅	|
| VUL4J-37	|	✅	|	✅	| 	✅	|
| VUL4J-38	|	✅	|	✅	| 	✅	|
| VUL4J-39	|	✅	|	✅	| 	✅	|
| VUL4J-40	|	✅	|	✅	| 	✅	|
| VUL4J-41	|	✅	|	✅	| 	✅	|
| VUL4J-42	|	✅	|	✅	| 	✅	|
| VUL4J-43	|	✅	|	✅	| 	✅	|
| VUL4J-44	|	✅	|	✅	| 	✅	|
| VUL4J-45	|	✅	|	✅	| 	✅	|
| VUL4J-46	|	✅	|	✅	| 	✅	|
| VUL4J-47	|	✅	|	✅	| 	✅	|
| VUL4J-48	|	✅	|	✅	| 	✅	|
| VUL4J-49	|	✅	|	✅	| 	✅	|
| VUL4J-50	|	✅	|	✅	| 	✅	|
| VUL4J-51	|	❌	|	❌	| 	❌	|
| VUL4J-52	|	✅	|	✅	| 	✅	|
| VUL4J-53	|	✅	|	✅	| 	✅	|
| VUL4J-54	|	✅	|	✅	| 	✅	|
| VUL4J-55	|	✅	|	✅	| 	✅	|
| VUL4J-56	|	✅	|	✅	| 	✅	|
| VUL4J-57	|	✅	|	✅	| 	✅	|
| VUL4J-58	|	✅	|	✅	| 	✅	|
| VUL4J-59	|	✅	|	✅	| 	✅	|
| VUL4J-60	|	✅	|	✅	| 	✅	|
| VUL4J-61	|	✅	|	✅	| 	✅	|
| VUL4J-62	|	✅	|	✅	| 	✅	|
| VUL4J-63	|	✅	|	✅	| 	✅	|
| VUL4J-64	|	✅	|	✅	| 	✅	|
| VUL4J-65	|	✅	|	✅	| 	✅	|
| VUL4J-66	|	✅	|	✅	| 	✅	|
| VUL4J-67	|	🖐	 |	 🖐	  |   🖐   |
| VUL4J-68	|	✅	|	✅	| 	✅	|
| VUL4J-69	|	✅	|	✅	| 	✅	|
| VUL4J-70	|	✅	|	✅	| 	✅	|
| VUL4J-71	|	🖐	 |	 🖐	  |   🖐   |
| VUL4J-72	|	✅	|	✅	| 	✅	|
| VUL4J-73	|	✅	|	✅	| 	✅	|
| VUL4J-74	|	✅	|	✅	| 	✅	|
| VUL4J-75	|	✅	|	✅	| 	✅	|
| VUL4J-76	|	✅	|	✅	| 	✅	|
| VUL4J-77	|	✅	|	✅	| 	✅	|
| VUL4J-78	|	✅	|	✅	| 	✅	|
| VUL4J-79	|	✅	|	✅	| 	✅	|
| **Total** |**60**  | **64** | **58 Fully**<br/> **6 Partial**(*)  | 