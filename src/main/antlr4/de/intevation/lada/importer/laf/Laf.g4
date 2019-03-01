grammar Laf;

probendatei : probe* end? EOF;

end : ENDOFLAF;

probe : '%PROBE%' (WS)* NEWLINE probedaten+ ;

probedaten: db
  | version
  | uebertragungsformat
  | netzkennung
  | erzeuger
  | sdm
  | messstelle
  | messlabor
  | probe_id
  | pnh
  | mp
  | messprogramm_land
  | probenahmeinstitution
  | probenart
  | zb
  | probenahme_datum_uhrzeit_a
  | probenahme_datum_uhrzeit_e
  | soll_datum_uhrzeit_a
  | soll_datum_uhrzeit_e
  | ursprungs_datum_uhrzeit
  | ub
  | deskriptoren
  | rei
  | referenz_datum_uhrzeit
  | testdaten
  | szenario
  | sek_datenbasis
  | sek_datenbasis_s
  | ursprungsort
  | entnahmeort
  | mehrzweckfeld
  | messung
  | pzb
  | pkom
  | emptyLine
;

messung:
  mess_header?
  messungdaten+
;

mess_header : '%MESSUNG%' (WS)* NEWLINE ;

messungdaten: messungs_id
  | pn
  | mess_datum_uhrzeit
  | messzeit_sekunden
  | mm
  | bearbeitungsstatus
  | pep_flag
  | erfassung_abgeschlossen
  | mw
  | mess_kommentar
  | emptyLine
;

db : datenbasis
   | datenbasis_s
   ;


mp : messprogramm_c
   | messprogramm_s
   ;

ub : umweltbereich_c
   | umweltbereich_s
   ;

rei : rei_programmpunkt
    | rei_programmpunktgruppe
    ;

entnahmeort: ph
  | pg
  | p_orts_zusatzkennzahl
  | pk
  | p_orts_zusatzcode
  | p_orts_zusatztext
  | p_nuts_code
  | p_site_id
  | p_site_name
  | p_hoehe_nn
  | p_hoehe_land
  | emptyLine
  ;

ph : p_herkunftsland_lang
   | p_herkunftsland_kurz
   | p_herkunftsland_s
   ;

pg : p_gemeindeschluessel
   | p_gemeindename
   ;

pk : p_koordinaten
   | p_koordinaten_s
   ;

zb : zeitbasis
   | zeitbasis_s
   ;

pzb : probenzusatzbeschreibung
    | pzb_s
    ;

pkom : probenkommentar
     | probenkommentar_t
     ;

sdm : staat_der_messstelle_lang
    | staat_der_messstelle_kurz
    | staat_der_messstelle_s
    ;

pnh : proben_nr
    | hauptprobennummer
    ;

// MESSUNG
mm : messmethode_c
   | messmethode_s
   ;

mw : messwert
   | messwert_s
   | messwert_i
   | messwert_g
   | messwert_nwg
   | messwert_nwg_s
   | messwert_nwg_i
   | messwert_nwg_g
   ;

mess_kommentar : kommentar
   | kommentar_t
   ;

pn : proben_nr
   | nebenprobennummer
   ;

// URSPRUNGSORT
ursprungsort:
  (ursprungsort_header |
  ursprungsortdaten+)
;

ursprungsort_header : '%URSPRUNGSORT%' (WS)* NEWLINE ;

ursprungsortdaten : uh
  | ug
  | u_orts_zusatzkennzahl
  | uk
  | u_orts_zusatzcode
  | u_orts_zusatztext
  | u_nuts_code
  | emptyLine
;

uh : u_herkunftsland_lang
   | u_herkunftsland_kurz
   | u_herkunftsland_s
   ;

ug : u_gemeindeschluessel
   | u_gemeindename
   ;

uk : u_koordinaten
   | u_koordinaten_s
   ;




version : VERSION (STRING_ESC | STRING)? NEWLINE ; // C4
uebertragungsformat : UEBERTRAGUNGSFORMAT (STRING_ESC | STRING)? NEWLINE ; // C1;

emptyLine : NEWLINE ;
datenbasis : DATENBASIS (STRING_ESC | STRING)? NEWLINE ; // C6
datenbasis_s : DATENBASIS_S (STRING_ESC | STRING)? NEWLINE ; // SI2
netzkennung : NETZKENNUNG (STRING_ESC | STRING)? NEWLINE ; // C2
erzeuger : ERZEUGER (STRING_ESC | STRING)? NEWLINE ; // C2
staat_der_messstelle_lang : STAAT_DER_MESSSTELLE_LANG (STRING_ESC | STRING)? NEWLINE ; //C50
staat_der_messstelle_kurz : STAAT_DER_MESSSTELLE_KURZ (STRING_ESC | STRING)? NEWLINE ; // C5
staat_der_messstelle_s : STAAT_DER_MESSSTELLE_S (STRING_ESC | STRING)? NEWLINE ; // SI8
messstelle : MESSSTELLE (STRING_ESC | STRING)? NEWLINE ; // SC5
messlabor : MESSLABOR (STRING_ESC | STRING)? NEWLINE ; // SC5
probe_id : PROBE_ID (STRING_ESC | STRING)? NEWLINE ; // C16
messungs_id : MESSUNGS_ID (STRING_ESC | STRING)? NEWLINE ; // I2
proben_nr : PROBEN_NR (STRING_ESC | STRING)? NEWLINE ; // C13
hauptprobennummer : HAUPTPROBENNUMMER (STRING_ESC | STRING)? NEWLINE ; // C20
nebenprobennummer : NEBENPROBENNUMMER (STRING_ESC | STRING)? NEWLINE ; // C4
messprogramm_c : MESSPROGRAMM_C (STRING_ESC | STRING)? NEWLINE ; // C50
messprogramm_s : MESSPROGRAMM_S (STRING_ESC | STRING)? NEWLINE ; // SC1
messprogramm_land : MESSPROGRAMM_LAND (STRING_ESC | STRING)? NEWLINE ; // C3
probenahmeinstitution : PROBENAHMEINSTITUTION (STRING_ESC | STRING)? NEWLINE ; // C9
probenart : PROBENART (STRING_ESC | STRING)? NEWLINE ; // C1
zeitbasis : ZEITBASIS (STRING_ESC | STRING)? NEWLINE ; // C30
zeitbasis_s : ZEITBASIS_S (STRING_ESC | STRING)? NEWLINE ; // SI1
soll_datum_uhrzeit_a : SOLL_DATUM_UHRZEIT_A ((STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // D8 T4
soll_datum_uhrzeit_e : SOLL_DATUM_UHRZEIT_E ((STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // D8 T4
ursprungs_datum_uhrzeit : URSPRUNGS_DATUM_UHRZEIT ((STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // D8 T4
probenahme_datum_uhrzeit_a : PROBENAHME_DATUM_UHRZEIT_A ((STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE; // D8 T4
probenahme_datum_uhrzeit_e : PROBENAHME_DATUM_UHRZEIT_E ((STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE; // D8 T4
umweltbereich_c : UMWELTBEREICH_C (STRING_ESC | STRING)? NEWLINE ; // C80
umweltbereich_s : UMWELTBEREICH_S (STRING_ESC | STRING)? NEWLINE ; // SC3
deskriptoren : DESKRIPTOREN (STRING_ESC | STRING)? NEWLINE ; // C26
rei_programmpunkt : REI_PROGRAMMPUNKT (STRING_ESC | STRING)? NEWLINE ; // C10
rei_programmpunktgruppe : REI_PROGRAMMPUNKTGRUPPE (STRING_ESC | STRING)? NEWLINE ; // C21
referenz_datum_uhrzeit : REFERENZ_DATUM_UHRZEIT (STRING STRING)? NEWLINE ; //D8 T4
testdaten : TESTDATEN (STRING_ESC | STRING)? NEWLINE  ; // I1 (0 or 1 represents a boolean value)
szenario : SZENARIO (STRING_ESC | STRING)? NEWLINE ; // C20
sek_datenbasis : SEK_DATENBASIS (STRING_ESC | STRING)? NEWLINE ; // C6*
sek_datenbasis_s : SEK_DATENBASIS_S (STRING_ESC | STRING)? NEWLINE ; //SI2
u_herkunftsland_lang : U_HERKUNFTSLAND_LANG (STRING_ESC | STRING)? NEWLINE ; //C50
u_herkunftsland_kurz : U_HERKUNFTSLAND_KURZ (STRING_ESC | STRING)? NEWLINE ; // C5
u_herkunftsland_s : U_HERKUNFTSLAND_S (STRING_ESC | STRING)? NEWLINE ; // SI8
u_gemeindeschluessel : U_GEMEINDESCHLUESSEL (STRING_ESC | STRING)? NEWLINE ; // I8
u_gemeindename : U_GEMEINDENAME (STRING_ESC | STRING)? NEWLINE ; // C80*
u_orts_zusatzkennzahl : U_ORTS_ZUSATZKENNZAHL (STRING_ESC | STRING)? NEWLINE ; // I3
u_koordinaten : U_KOORDINATEN ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // C50 C22 C22
u_koordinaten_s : U_KOORDINATEN_S ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // SI2 C22 C22
u_orts_zusatzcode : U_ORTS_ZUSATZCODE (STRING_ESC | STRING)? NEWLINE ; // C8
u_orts_zusatztext : U_ORTS_ZUSATZTEXT (STRING_ESC | STRING)? NEWLINE ; // MC50
u_nuts_code : U_NUTS_CODE (STRING_ESC | STRING)? NEWLINE ; // C10
p_herkunftsland_lang : P_HERKUNFTSLAND_LANG (STRING_ESC | STRING)? NEWLINE ; //C50*
p_herkunftsland_kurz : P_HERKUNFTSLAND_KURZ (STRING_ESC | STRING)? NEWLINE ; // C5
p_herkunftsland_s : P_HERKUNFTSLAND_S (STRING_ESC | STRING)? NEWLINE ; // SI8
p_gemeindeschluessel : P_GEMEINDESCHLUESSEL (STRING_ESC | STRING)? NEWLINE ; // I8
p_gemeindename : P_GEMEINDENAME (STRING_ESC | STRING)? NEWLINE ; // C80*
p_orts_zusatzkennzahl : P_ORTS_ZUSATZKENNZAHL (STRING_ESC | STRING)? NEWLINE ; // I3
p_koordinaten : P_KOORDINATEN ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // C50* C22 C22
p_koordinaten_s : P_KOORDINATEN_S ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // SI2 C22 C22
p_orts_zusatzcode : P_ORTS_ZUSATZCODE (STRING_ESC | STRING)? NEWLINE ; // C8
p_orts_zusatztext : P_ORTS_ZUSATZTEXT (STRING_ESC | STRING)? NEWLINE ; // MC50
p_nuts_code : P_NUTS_CODE (STRING_ESC | STRING)? NEWLINE ; // C10
p_site_id : P_SITE_ID (STRING_ESC | STRING)? NEWLINE ; // C8
p_site_name : P_SITE_NAME (STRING_ESC | STRING)? NEWLINE ; // C50*
p_hoehe_nn : P_HOEHE_NN (STRING_ESC | STRING)? NEWLINE ; // F10
p_hoehe_land : P_HOEHE_LAND (STRING_ESC | STRING)? NEWLINE ; // F10
mehrzweckfeld : MEHRZWECKFELD (STRING_ESC | STRING)? NEWLINE ; // MC300
mess_datum_uhrzeit : MESS_DATUM_UHRZEIT ((STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // D8 T4
messzeit_sekunden : MESSZEIT_SEKUNDEN (STRING_ESC | STRING)? NEWLINE ; // I8
messmethode_c : MESSMETHODE_C (STRING_ESC | STRING)? NEWLINE ; // C50*
messmethode_s : MESSMETHODE_S (STRING_ESC | STRING)? NEWLINE ; // SC2
bearbeitungsstatus : BEARBEITUNGSSTATUS (STRING_ESC | STRING)? NEWLINE ; // C4
pep_flag : PEP_FLAG (STRING_ESC | STRING)? NEWLINE ; // I1
erfassung_abgeschlossen : ERFASSUNG_ABGESCHLOSSEN (STRING_ESC | STRING)? NEWLINE ; // I1
probenzusatzbeschreibung : PROBENZUSATZBESCHREIBUNG ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING)?)? NEWLINE ; // C7* F12 C9 F9
pzb_s : PZB_S ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // SC8 F12 SI3 F9
messwert : MESSWERT ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING)?)? NEWLINE ; // C50* F12 C9 F9**
messwert_s : MESSWERT_S ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING)?)? NEWLINE ; // SI8 F12 SI3 F9**
messwert_i : MESSWERT_I ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING)? (STRING_ESC | STRING)? (STRING_ESC | STRING)? (STRING_ESC | STRING)?)? NEWLINE ; // C50* F12 C9 F9** F9** F9** C50*
messwert_g : MESSWERT_G ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING)? (STRING_ESC | STRING)? (STRING_ESC | STRING)? (STRING_ESC | STRING)?)? NEWLINE ; // C50* F12 C9 F9** F9** F9** C1
messwert_nwg : MESSWERT_NWG ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING)? (STRING_ESC | STRING)?)? NEWLINE ; // C50* F12 C9 F9** F12
messwert_nwg_s : MESSWERT_NWG_S ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING)? (STRING_ESC | STRING)?)? NEWLINE ; // SI8 F12 SI3 F9** F12
messwert_nwg_i : MESSWERT_NWG_I ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING)? (STRING_ESC | STRING)? (STRING_ESC | STRING)? (STRING_ESC | STRING)? (STRING_ESC | STRING)?)? NEWLINE ; // C50* F12 C9 F9** F12 F9** F9** C50
messwert_nwg_g : MESSWERT_NWG_G ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING)? (STRING_ESC | STRING)? (STRING_ESC | STRING)? (STRING_ESC | STRING)? (STRING_ESC | STRING)?)? NEWLINE ; // C50* F12 C9 F9** F12 F9** F9** C1
kommentar : KOMMENTAR ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // C5 D8 T4 MC300
kommentar_t : KOMMENTAR_T (STRING_ESC | STRING)? NEWLINE ; // MC300
probenkommentar : PROBENKOMMENTAR ((STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING) (STRING_ESC | STRING))? NEWLINE ; // C5 D8 T4 MC300
probenkommentar_t : PROBENKOMMENTAR_T (STRING_ESC | STRING)? NEWLINE ; // MC300

VERSION : ('V'|'v')('E'|'e')('R'|'r')('S'|'s')('I'|'i')('O'|'o')('N'|'n') ;
UEBERTRAGUNGSFORMAT : ('U'|'u')('E'|'e')('B'|'b')('E'|'e')('R'|'r')('T'|'t')('R'|'r')('A'|'a')('G'|'g')('U'|'u')('N'|'n')('G'|'g')('S'|'s')('F'|'f')('O'|'o')('R'|'r')('M'|'m')('A'|'a')('T'|'t') ;
DATENBASIS : ('D'|'d')('A'|'a')('T'|'t')('E'|'e')('N'|'n')('B'|'b')('A'|'a')('S'|'s')('I'|'i')('S'|'s') ;
DATENBASIS_S : ('D'|'d')('A'|'a')('T'|'t')('E'|'e')('N'|'n')('B'|'b')('A'|'a')('S'|'s')('I'|'i')('S'|'s')('_')('S'|'s') ;
NETZKENNUNG : ('N'|'n')('E'|'e')('T'|'t')('Z'|'z')('K'|'k')('E'|'e')('N'|'n')('N'|'n')('U'|'u')('N'|'n')('G'|'g') ;
ERZEUGER : ('E'|'e')('R'|'r')('Z'|'z')('E'|'e')('U'|'u')('G'|'g')('E'|'e')('R'|'r') ;
STAAT_DER_MESSSTELLE_LANG : ('S'|'s')('T'|'t')('A'|'a')('A'|'a')('T'|'t')('_')('D'|'d')('E'|'e')('R'|'r')('_')('M'|'m')('E'|'e')('S'|'s')('S'|'s')('S'|'s')('T'|'t')('E'|'e')('L'|'l')('L'|'l')('E'|'e')('_')('L'|'l')('A'|'a')('N'|'n')('G'|'g') ;
STAAT_DER_MESSSTELLE_KURZ : ('S'|'s')('T'|'t')('A'|'a')('A'|'a')('T'|'t')('_')('D'|'d')('E'|'e')('R'|'r')('_')('M'|'m')('E'|'e')('S'|'s')('S'|'s')('S'|'s')('T'|'t')('E'|'e')('L'|'l')('L'|'l')('E'|'e')('_')('K'|'k')('U'|'u')('R'|'r')('Z'|'z') ;
STAAT_DER_MESSSTELLE_S : ('S'|'s')('T'|'t')('A'|'a')('A'|'a')('T'|'t')('_')('D'|'d')('E'|'e')('R'|'r')('_')('M'|'m')('E'|'e')('S'|'s')('S'|'s')('S'|'s')('T'|'t')('E'|'e')('L'|'l')('L'|'l')('E'|'e')('_')('S'|'s') ;
MESSSTELLE : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('S'|'s')('T'|'t')('E'|'e')('L'|'l')('L'|'l')('E'|'e') ;
MESSLABOR : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('L'|'l')('A'|'a')('B'|'b')('O'|'o')('R'|'r') ;
PROBE_ID : ('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('_')('I'|'i')('D'|'d') ;
MESSUNGS_ID : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('U'|'u')('N'|'n')('G'|'g')('S'|'s')('_')('I'|'i')('D'|'d') ;
PROBEN_NR : ('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('N'|'n')('_')('N'|'n')('R'|'r') ;
HAUPTPROBENNUMMER : ('H'|'h')('A'|'a')('U'|'u')('P'|'p')('T'|'t')('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('N'|'n')('N'|'n')('U'|'u')('M'|'m')('M'|'m')('E'|'e')('R'|'r') ;
NEBENPROBENNUMMER : ('N'|'n')('E'|'e')('B'|'b')('E'|'e')('N'|'n')('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('N'|'n')('N'|'n')('U'|'u')('M'|'m')('M'|'m')('E'|'e')('R'|'r') ;
MESSPROGRAMM_C : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('P'|'p')('R'|'r')('O'|'o')('G'|'g')('R'|'r')('A'|'a')('M'|'m')('M'|'m')('_')('C'|'c') ;
MESSPROGRAMM_S : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('P'|'p')('R'|'r')('O'|'o')('G'|'g')('R'|'r')('A'|'a')('M'|'m')('M'|'m')('_')('S'|'s') ;
MESSPROGRAMM_LAND : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('P'|'p')('R'|'r')('O'|'o')('G'|'g')('R'|'r')('A'|'a')('M'|'m')('M'|'m')('_')('L'|'l')('A'|'a')('N'|'n')('D'|'d') ;
PROBENAHMEINSTITUTION : ('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('N'|'n')('A'|'a')('H'|'h')('M'|'m')('E'|'e')('I'|'i')('N'|'n')('S'|'s')('T'|'t')('I'|'i')('T'|'t')('U'|'u')('T'|'t')('I'|'i')('O'|'o')('N'|'n') ;
PROBENART : ('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('N'|'n')('A'|'a')('R'|'r')('T'|'t') ;
ZEITBASIS : ('Z'|'z')('E'|'e')('I'|'i')('T'|'t')('B'|'b')('A'|'a')('S'|'s')('I'|'i')('S'|'s') ;
ZEITBASIS_S : ('Z'|'z')('E'|'e')('I'|'i')('T'|'t')('B'|'b')('A'|'a')('S'|'s')('I'|'i')('S'|'s')('_')('S'|'s') ;
SOLL_DATUM_UHRZEIT_A : ('S'|'s')('O'|'o')('L'|'l')('L'|'l')('_')('D'|'d')('A'|'a')('T'|'t')('U'|'u')('M'|'m')('_')('U'|'u')('H'|'h')('R'|'r')('Z'|'z')('E'|'e')('I'|'i')('T'|'t')('_')('A'|'a') ;
SOLL_DATUM_UHRZEIT_E : ('S'|'s')('O'|'o')('L'|'l')('L'|'l')('_')('D'|'d')('A'|'a')('T'|'t')('U'|'u')('M'|'m')('_')('U'|'u')('H'|'h')('R'|'r')('Z'|'z')('E'|'e')('I'|'i')('T'|'t')('_')('E'|'e') ;
URSPRUNGS_DATUM_UHRZEIT : ('U'|'u')('R'|'r')('S'|'s')('P'|'p')('R'|'r')('U'|'u')('N'|'n')('G'|'g')('S'|'s')('_')('D'|'d')('A'|'a')('T'|'t')('U'|'u')('M'|'m')('_')('U'|'u')('H'|'h')('R'|'r')('Z'|'z')('E'|'e')('I'|'i')('T'|'t') ;
PROBENAHME_DATUM_UHRZEIT_A : ('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('N'|'n')('A'|'a')('H'|'h')('M'|'m')('E'|'e')('_')('D'|'d')('A'|'a')('T'|'t')('U'|'u')('M'|'m')('_')('U'|'u')('H'|'h')('R'|'r')('Z'|'z')('E'|'e')('I'|'i')('T'|'t')('_')('A'|'a') ;
PROBENAHME_DATUM_UHRZEIT_E : ('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('N'|'n')('A'|'a')('H'|'h')('M'|'m')('E'|'e')('_')('D'|'d')('A'|'a')('T'|'t')('U'|'u')('M'|'m')('_')('U'|'u')('H'|'h')('R'|'r')('Z'|'z')('E'|'e')('I'|'i')('T'|'t')('_')('E'|'e') ;
UMWELTBEREICH_C : ('U'|'u')('M'|'m')('W'|'w')('E'|'e')('L'|'l')('T'|'t')('B'|'b')('E'|'e')('R'|'r')('E'|'e')('I'|'i')('C'|'c')('H'|'h')('_')('C'|'c') ;
UMWELTBEREICH_S : ('U'|'u')('M'|'m')('W'|'w')('E'|'e')('L'|'l')('T'|'t')('B'|'b')('E'|'e')('R'|'r')('E'|'e')('I'|'i')('C'|'c')('H'|'h')('_')('S'|'s') ;
DESKRIPTOREN : ('D'|'d')('E'|'e')('S'|'s')('K'|'k')('R'|'r')('I'|'i')('P'|'p')('T'|'t')('O'|'o')('R'|'r')('E'|'e')('N'|'n') ;
REI_PROGRAMMPUNKT : ('R'|'r')('E'|'e')('I'|'i')('_')('P'|'p')('R'|'r')('O'|'o')('G'|'g')('R'|'r')('A'|'a')('M'|'m')('M'|'m')('P'|'p')('U'|'u')('N'|'n')('K'|'k')('T'|'t') ;
REI_PROGRAMMPUNKTGRUPPE : ('R'|'r')('E'|'e')('I'|'i')('_')('P'|'p')('R'|'r')('O'|'o')('G'|'g')('R'|'r')('A'|'a')('M'|'m')('M'|'m')('P'|'p')('U'|'u')('N'|'n')('K'|'k')('T'|'t')('G'|'g')('R'|'r')('U'|'u')('P'|'p')('P'|'p')('E'|'e') ;
REFERENZ_DATUM_UHRZEIT : ('R'|'r')('E'|'e')('F'|'f')('E'|'e')('R'|'r')('E'|'e')('N'|'n')('Z'|'z')('_')('D'|'d')('A'|'a')('T'|'t')('U'|'u')('M'|'m')('_')('U'|'u')('H'|'h')('R'|'r')('Z'|'z')('E'|'e')('I'|'i')('T'|'t') ;
TESTDATEN : ('T'|'t')('E'|'e')('S'|'s')('T'|'t')('D'|'d')('A'|'a')('T'|'t')('E'|'e')('N'|'n') ;
SZENARIO : ('S'|'s')('Z'|'z')('E'|'e')('N'|'n')('A'|'a')('R'|'r')('I'|'i')('O'|'o') ;
SEK_DATENBASIS : ('S'|'s')('E'|'e')('K'|'k')('_')('D'|'d')('A'|'a')('T'|'t')('E'|'e')('N'|'n')('B'|'b')('A'|'a')('S'|'s')('I'|'i')('S'|'s') ;
SEK_DATENBASIS_S : ('S'|'s')('E'|'e')('K'|'k')('_')('D'|'d')('A'|'a')('T'|'t')('E'|'e')('N'|'n')('B'|'b')('A'|'a')('S'|'s')('I'|'i')('S'|'s')('_')('S'|'s') ;
U_HERKUNFTSLAND_LANG : ('U'|'u')('_')('H'|'h')('E'|'e')('R'|'r')('K'|'k')('U'|'u')('N'|'n')('F'|'f')('T'|'t')('S'|'s')('L'|'l')('A'|'a')('N'|'n')('D'|'d')('_')('L'|'l')('A'|'a')('N'|'n')('G'|'g') ;
U_HERKUNFTSLAND_KURZ : ('U'|'u')('_')('H'|'h')('E'|'e')('R'|'r')('K'|'k')('U'|'u')('N'|'n')('F'|'f')('T'|'t')('S'|'s')('L'|'l')('A'|'a')('N'|'n')('D'|'d')('_')('K'|'k')('U'|'u')('R'|'r')('Z'|'z') ;
U_HERKUNFTSLAND_S : ('U'|'u')('_')('H'|'h')('E'|'e')('R'|'r')('K'|'k')('U'|'u')('N'|'n')('F'|'f')('T'|'t')('S'|'s')('L'|'l')('A'|'a')('N'|'n')('D'|'d')('_')('S'|'s') ;
U_GEMEINDESCHLUESSEL : ('U'|'u')('_')('G'|'g')('E'|'e')('M'|'m')('E'|'e')('I'|'i')('N'|'n')('D'|'d')('E'|'e')('S'|'s')('C'|'c')('H'|'h')('L'|'l')('U'|'u')('E'|'e')('S'|'s')('S'|'s')('E'|'e')('L'|'l') ;
U_GEMEINDENAME : ('U'|'u')('_')('G'|'g')('E'|'e')('M'|'m')('E'|'e')('I'|'i')('N'|'n')('D'|'d')('E'|'e')('N'|'n')('A'|'a')('M'|'m')('E'|'e') ;
U_ORTS_ZUSATZKENNZAHL : ('U'|'u')('_')('O'|'o')('R'|'r')('T'|'t')('S'|'s')('_')('Z'|'z')('U'|'u')('S'|'s')('A'|'a')('T'|'t')('Z'|'z')('K'|'k')('E'|'e')('N'|'n')('N'|'n')('Z'|'z')('A'|'a')('H'|'h')('L'|'l') ;
U_KOORDINATEN : ('U'|'u')('_')('K'|'k')('O'|'o')('O'|'o')('R'|'r')('D'|'d')('I'|'i')('N'|'n')('A'|'a')('T'|'t')('E'|'e')('N'|'n') ;
U_KOORDINATEN_S : ('U'|'u')('_')('K'|'k')('O'|'o')('O'|'o')('R'|'r')('D'|'d')('I'|'i')('N'|'n')('A'|'a')('T'|'t')('E'|'e')('N'|'n')('_')('S'|'s') ;
U_ORTS_ZUSATZCODE : ('U'|'u')('_')('O'|'o')('R'|'r')('T'|'t')('S'|'s')('_')('Z'|'z')('U'|'u')('S'|'s')('A'|'a')('T'|'t')('Z'|'z')('C'|'c')('O'|'o')('D'|'d')('E'|'e') ;
U_ORTS_ZUSATZTEXT : ('U'|'u')('_')('O'|'o')('R'|'r')('T'|'t')('S'|'s')('_')('Z'|'z')('U'|'u')('S'|'s')('A'|'a')('T'|'t')('Z'|'z')('T'|'t')('E'|'e')('X'|'x')('T'|'t') ;
U_NUTS_CODE : ('U'|'u')('_')('N'|'n')('U'|'u')('T'|'t')('S'|'s')('_')('C'|'c')('O'|'o')('D'|'d')('E'|'e') ;
P_HERKUNFTSLAND_LANG : ('P'|'p')('_')('H'|'h')('E'|'e')('R'|'r')('K'|'k')('U'|'u')('N'|'n')('F'|'f')('T'|'t')('S'|'s')('L'|'l')('A'|'a')('N'|'n')('D'|'d')('_')('L'|'l')('A'|'a')('N'|'n')('G'|'g') ;
P_HERKUNFTSLAND_KURZ : ('P'|'p')('_')('H'|'h')('E'|'e')('R'|'r')('K'|'k')('U'|'u')('N'|'n')('F'|'f')('T'|'t')('S'|'s')('L'|'l')('A'|'a')('N'|'n')('D'|'d')('_')('K'|'k')('U'|'u')('R'|'r')('Z'|'z') ;
P_HERKUNFTSLAND_S : ('P'|'p')('_')('H'|'h')('E'|'e')('R'|'r')('K'|'k')('U'|'u')('N'|'n')('F'|'f')('T'|'t')('S'|'s')('L'|'l')('A'|'a')('N'|'n')('D'|'d')('_')('S'|'s') ;
P_GEMEINDESCHLUESSEL : ('P'|'p')('_')('G'|'g')('E'|'e')('M'|'m')('E'|'e')('I'|'i')('N'|'n')('D'|'d')('E'|'e')('S'|'s')('C'|'c')('H'|'h')('L'|'l')('U'|'u')('E'|'e')('S'|'s')('S'|'s')('E'|'e')('L'|'l') ;
P_GEMEINDENAME : ('P'|'p')('_')('G'|'g')('E'|'e')('M'|'m')('E'|'e')('I'|'i')('N'|'n')('D'|'d')('E'|'e')('N'|'n')('A'|'a')('M'|'m')('E'|'e') ;
P_ORTS_ZUSATZKENNZAHL : ('P'|'p')('_')('O'|'o')('R'|'r')('T'|'t')('S'|'s')('_')('Z'|'z')('U'|'u')('S'|'s')('A'|'a')('T'|'t')('Z'|'z')('K'|'k')('E'|'e')('N'|'n')('N'|'n')('Z'|'z')('A'|'a')('H'|'h')('L'|'l') ;
P_KOORDINATEN : ('P'|'p')('_')('K'|'k')('O'|'o')('O'|'o')('R'|'r')('D'|'d')('I'|'i')('N'|'n')('A'|'a')('T'|'t')('E'|'e')('N'|'n') ;
P_KOORDINATEN_S : ('P'|'p')('_')('K'|'k')('O'|'o')('O'|'o')('R'|'r')('D'|'d')('I'|'i')('N'|'n')('A'|'a')('T'|'t')('E'|'e')('N'|'n')('_')('S'|'s') ;
P_ORTS_ZUSATZCODE : ('P'|'p')('_')('O'|'o')('R'|'r')('T'|'t')('S'|'s')('_')('Z'|'z')('U'|'u')('S'|'s')('A'|'a')('T'|'t')('Z'|'z')('C'|'c')('O'|'o')('D'|'d')('E'|'e') ;
P_ORTS_ZUSATZTEXT : ('P'|'p')('_')('O'|'o')('R'|'r')('T'|'t')('S'|'s')('_')('Z'|'z')('U'|'u')('S'|'s')('A'|'a')('T'|'t')('Z'|'z')('T'|'t')('E'|'e')('X'|'x')('T'|'t') ;
P_NUTS_CODE : ('P'|'p')('_')('N'|'n')('U'|'u')('T'|'t')('S'|'s')('_')('C'|'c')('O'|'o')('D'|'d')('E'|'e') ;
P_SITE_ID : ('P'|'p')('_')('S'|'s')('I'|'i')('T'|'t')('E'|'e')('_')('I'|'i')('D'|'d') ;
P_SITE_NAME : ('P'|'p')('_')('S'|'s')('I'|'i')('T'|'t')('E'|'e')('_')('N'|'n')('A'|'a')('M'|'m')('E'|'e') ;
P_HOEHE_NN : ('P'|'p')('_')('H'|'h')('O'|'o')('E'|'e')('H'|'h')('E'|'e')('_')('N'|'n')('N'|'n') ;
P_HOEHE_LAND : ('P'|'p')('_')('H'|'h')('O'|'o')('E'|'e')('H'|'h')('E'|'e')('_')('L'|'l')('A'|'a')('N'|'n')('D'|'d') ;
MEHRZWECKFELD : ('M'|'m')('E'|'e')('H'|'h')('R'|'r')('Z'|'z')('W'|'w')('E'|'e')('C'|'c')('K'|'k')('F'|'f')('E'|'e')('L'|'l')('D'|'d') ;
MESS_DATUM_UHRZEIT : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('_')('D'|'d')('A'|'a')('T'|'t')('U'|'u')('M'|'m')('_')('U'|'u')('H'|'h')('R'|'r')('Z'|'z')('E'|'e')('I'|'i')('T'|'t') ;
MESSZEIT_SEKUNDEN : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('Z'|'z')('E'|'e')('I'|'i')('T'|'t')('_')('S'|'s')('E'|'e')('K'|'k')('U'|'u')('N'|'n')('D'|'d')('E'|'e')('N'|'n') ;
MESSMETHODE_C : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('M'|'m')('E'|'e')('T'|'t')('H'|'h')('O'|'o')('D'|'d')('E'|'e')('_')('C'|'c') ;
MESSMETHODE_S : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('M'|'m')('E'|'e')('T'|'t')('H'|'h')('O'|'o')('D'|'d')('E'|'e')('_')('S'|'s') ;
BEARBEITUNGSSTATUS : ('B'|'b')('E'|'e')('A'|'a')('R'|'r')('B'|'b')('E'|'e')('I'|'i')('T'|'t')('U'|'u')('N'|'n')('G'|'g')('S'|'s')('S'|'s')('T'|'t')('A'|'a')('T'|'t')('U'|'u')('S'|'s') ;
PEP_FLAG : ('P'|'p')('E'|'e')('P'|'p')('_')('F'|'f')('L'|'l')('A'|'a')('G'|'g') ;
ERFASSUNG_ABGESCHLOSSEN : ('E'|'e')('R'|'r')('F'|'f')('A'|'a')('S'|'s')('S'|'s')('U'|'u')('N'|'n')('G'|'g')('_')('A'|'a')('B'|'b')('G'|'g')('E'|'e')('S'|'s')('C'|'c')('H'|'h')('L'|'l')('O'|'o')('S'|'s')('S'|'s')('E'|'e')('N'|'n') ;
PROBENZUSATZBESCHREIBUNG : ('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('N'|'n')('Z'|'z')('U'|'u')('S'|'s')('A'|'a')('T'|'t')('Z'|'z')('B'|'b')('E'|'e')('S'|'s')('C'|'c')('H'|'h')('R'|'r')('E'|'e')('I'|'i')('B'|'b')('U'|'u')('N'|'n')('G'|'g') ;
PZB_S : ('P'|'p')('Z'|'z')('B'|'b')('_')('S'|'s') ;
MESSWERT : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('W'|'w')('E'|'e')('R'|'r')('T'|'t') ;
MESSWERT_S : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('W'|'w')('E'|'e')('R'|'r')('T'|'t')('_')('S'|'s') ;
MESSWERT_I : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('W'|'w')('E'|'e')('R'|'r')('T'|'t')('_')('I'|'i') ;
MESSWERT_G : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('W'|'w')('E'|'e')('R'|'r')('T'|'t')('_')('G'|'g') ;
MESSWERT_NWG : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('W'|'w')('E'|'e')('R'|'r')('T'|'t')('_')('N'|'n')('W'|'w')('G'|'g') ;
MESSWERT_NWG_S : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('W'|'w')('E'|'e')('R'|'r')('T'|'t')('_')('N'|'n')('W'|'w')('G'|'g')('_')('S'|'s') ;
MESSWERT_NWG_I : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('W'|'w')('E'|'e')('R'|'r')('T'|'t')('_')('N'|'n')('W'|'w')('G'|'g')('_')('I'|'i') ;
MESSWERT_NWG_G : ('M'|'m')('E'|'e')('S'|'s')('S'|'s')('W'|'w')('E'|'e')('R'|'r')('T'|'t')('_')('N'|'n')('W'|'w')('G'|'g')('_')('G'|'g') ;
KOMMENTAR : ('K'|'k')('O'|'o')('M'|'m')('M'|'m')('E'|'e')('N'|'n')('T'|'t')('A'|'a')('R'|'r') ;
KOMMENTAR_T : ('K'|'k')('O'|'o')('M'|'m')('M'|'m')('E'|'e')('N'|'n')('T'|'t')('A'|'a')('R'|'r')('_')('T'|'t') ;
PROBENKOMMENTAR : ('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('N'|'n')('K'|'k')('O'|'o')('M'|'m')('M'|'m')('E'|'e')('N'|'n')('T'|'t')('A'|'a')('R'|'r') ;
PROBENKOMMENTAR_T : ('P'|'p')('R'|'r')('O'|'o')('B'|'b')('E'|'e')('N'|'n')('K'|'k')('O'|'o')('M'|'m')('M'|'m')('E'|'e')('N'|'n')('T'|'t')('A'|'a')('R'|'r')('_')('T'|'t') ;


WS : (' ' | '\t') -> skip;
NEWLINE : ('\r\n' | '\r' | '\n') ;
STRING_ESC : ('"'(~('"'))*'"') ;
STRING : C+ ;
C : CHAR ;
fragment CHAR : ~[ \t"\r\n] ;
ENDOFLAF : '%ENDE%' .* ;
