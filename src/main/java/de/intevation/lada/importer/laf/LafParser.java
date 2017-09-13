// Generated from de/intevation/lada/importer/laf/Laf.g4 by ANTLR 4.5
package de.intevation.lada.importer.laf;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LafParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, VERSION=5, UEBERTRAGUNGSFORMAT=6, DATENBASIS=7, 
		DATENBASIS_S=8, NETZKENNUNG=9, ERZEUGER=10, STAAT_DER_MESSSTELLE_LANG=11, 
		STAAT_DER_MESSSTELLE_KURZ=12, STAAT_DER_MESSSTELLE_S=13, MESSSTELLE=14, 
		MESSLABOR=15, PROBE_ID=16, MESSUNGS_ID=17, PROBEN_NR=18, HAUPTPROBENNUMMER=19, 
		NEBENPROBENNUMMER=20, MESSPROGRAMM_C=21, MESSPROGRAMM_S=22, MESSPROGRAMM_LAND=23, 
		PROBENAHMEINSTITUTION=24, PROBENART=25, ZEITBASIS=26, ZEITBASIS_S=27, 
		SOLL_DATUM_UHRZEIT_A=28, SOLL_DATUM_UHRZEIT_E=29, URSPRUNGS_DATUM_UHRZEIT=30, 
		PROBENAHME_DATUM_UHRZEIT_A=31, PROBENAHME_DATUM_UHRZEIT_E=32, UMWELTBEREICH_C=33, 
		UMWELTBEREICH_S=34, DESKRIPTOREN=35, REI_PROGRAMMPUNKT=36, REI_PROGRAMMPUNKTGRUPPE=37, 
		REFERENZ_DATUM_UHRZEIT=38, TESTDATEN=39, SZENARIO=40, SEK_DATENBASIS=41, 
		SEK_DATENBASIS_S=42, U_HERKUNFTSLAND_LANG=43, U_HERKUNFTSLAND_KURZ=44, 
		U_HERKUNFTSLAND_S=45, U_GEMEINDESCHLUESSEL=46, U_GEMEINDENAME=47, U_ORTS_ZUSATZKENNZAHL=48, 
		U_KOORDINATEN=49, U_KOORDINATEN_S=50, U_ORTS_ZUSATZCODE=51, U_ORTS_ZUSATZTEXT=52, 
		U_NUTS_CODE=53, P_HERKUNFTSLAND_LANG=54, P_HERKUNFTSLAND_KURZ=55, P_HERKUNFTSLAND_S=56, 
		P_GEMEINDESCHLUESSEL=57, P_GEMEINDENAME=58, P_ORTS_ZUSATZKENNZAHL=59, 
		P_KOORDINATEN=60, P_KOORDINATEN_S=61, P_ORTS_ZUSATZCODE=62, P_ORTS_ZUSATZTEXT=63, 
		P_NUTS_CODE=64, P_SITE_ID=65, P_SITE_NAME=66, P_HOEHE_NN=67, P_HOEHE_LAND=68, 
		MEHRZWECKFELD=69, MESS_DATUM_UHRZEIT=70, MESSZEIT_SEKUNDEN=71, MESSMETHODE_C=72, 
		MESSMETHODE_S=73, BEARBEITUNGSSTATUS=74, PEP_FLAG=75, ERFASSUNG_ABGESCHLOSSEN=76, 
		PROBENZUSATZBESCHREIBUNG=77, PZB_S=78, MESSWERT=79, MESSWERT_S=80, MESSWERT_I=81, 
		MESSWERT_G=82, MESSWERT_NWG=83, MESSWERT_NWG_S=84, MESSWERT_NWG_I=85, 
		MESSWERT_NWG_G=86, KOMMENTAR=87, KOMMENTAR_T=88, PROBENKOMMENTAR=89, PROBENKOMMENTAR_T=90, 
		WS=91, NEWLINE=92, STRING_ESC=93, STRING=94, C=95;
	public static final int
		RULE_probendatei = 0, RULE_end = 1, RULE_probe = 2, RULE_probedaten = 3, 
		RULE_messung = 4, RULE_mess_header = 5, RULE_messungdaten = 6, RULE_db = 7, 
		RULE_mp = 8, RULE_ub = 9, RULE_rei = 10, RULE_entnahmeort = 11, RULE_ph = 12, 
		RULE_pg = 13, RULE_pk = 14, RULE_zb = 15, RULE_pzb = 16, RULE_pkom = 17, 
		RULE_sdm = 18, RULE_pnh = 19, RULE_mm = 20, RULE_mw = 21, RULE_mess_kommentar = 22, 
		RULE_pn = 23, RULE_ursprungsort = 24, RULE_ursprungsort_header = 25, RULE_ursprungsortdaten = 26, 
		RULE_uh = 27, RULE_ug = 28, RULE_uk = 29, RULE_version = 30, RULE_uebertragungsformat = 31, 
		RULE_datenbasis = 32, RULE_datenbasis_s = 33, RULE_netzkennung = 34, RULE_erzeuger = 35, 
		RULE_staat_der_messstelle_lang = 36, RULE_staat_der_messstelle_kurz = 37, 
		RULE_staat_der_messstelle_s = 38, RULE_messstelle = 39, RULE_messlabor = 40, 
		RULE_probe_id = 41, RULE_messungs_id = 42, RULE_proben_nr = 43, RULE_hauptprobennummer = 44, 
		RULE_nebenprobennummer = 45, RULE_messprogramm_c = 46, RULE_messprogramm_s = 47, 
		RULE_messprogramm_land = 48, RULE_probenahmeinstitution = 49, RULE_probenart = 50, 
		RULE_zeitbasis = 51, RULE_zeitbasis_s = 52, RULE_soll_datum_uhrzeit_a = 53, 
		RULE_soll_datum_uhrzeit_e = 54, RULE_ursprungs_datum_uhrzeit = 55, RULE_probenahme_datum_uhrzeit_a = 56, 
		RULE_probenahme_datum_uhrzeit_e = 57, RULE_umweltbereich_c = 58, RULE_umweltbereich_s = 59, 
		RULE_deskriptoren = 60, RULE_rei_programmpunkt = 61, RULE_rei_programmpunktgruppe = 62, 
		RULE_referenz_datum_uhrzeit = 63, RULE_testdaten = 64, RULE_szenario = 65, 
		RULE_sek_datenbasis = 66, RULE_sek_datenbasis_s = 67, RULE_u_herkunftsland_lang = 68, 
		RULE_u_herkunftsland_kurz = 69, RULE_u_herkunftsland_s = 70, RULE_u_gemeindeschluessel = 71, 
		RULE_u_gemeindename = 72, RULE_u_orts_zusatzkennzahl = 73, RULE_u_koordinaten = 74, 
		RULE_u_koordinaten_s = 75, RULE_u_orts_zusatzcode = 76, RULE_u_orts_zusatztext = 77, 
		RULE_u_nuts_code = 78, RULE_p_herkunftsland_lang = 79, RULE_p_herkunftsland_kurz = 80, 
		RULE_p_herkunftsland_s = 81, RULE_p_gemeindeschluessel = 82, RULE_p_gemeindename = 83, 
		RULE_p_orts_zusatzkennzahl = 84, RULE_p_koordinaten = 85, RULE_p_koordinaten_s = 86, 
		RULE_p_orts_zusatzcode = 87, RULE_p_orts_zusatztext = 88, RULE_p_nuts_code = 89, 
		RULE_p_site_id = 90, RULE_p_site_name = 91, RULE_p_hoehe_nn = 92, RULE_p_hoehe_land = 93, 
		RULE_mehrzweckfeld = 94, RULE_mess_datum_uhrzeit = 95, RULE_messzeit_sekunden = 96, 
		RULE_messmethode_c = 97, RULE_messmethode_s = 98, RULE_bearbeitungsstatus = 99, 
		RULE_pep_flag = 100, RULE_erfassung_abgeschlossen = 101, RULE_probenzusatzbeschreibung = 102, 
		RULE_pzb_s = 103, RULE_messwert = 104, RULE_messwert_s = 105, RULE_messwert_i = 106, 
		RULE_messwert_g = 107, RULE_messwert_nwg = 108, RULE_messwert_nwg_s = 109, 
		RULE_messwert_nwg_i = 110, RULE_messwert_nwg_g = 111, RULE_kommentar = 112, 
		RULE_kommentar_t = 113, RULE_probenkommentar = 114, RULE_probenkommentar_t = 115;
	public static final String[] ruleNames = {
		"probendatei", "end", "probe", "probedaten", "messung", "mess_header", 
		"messungdaten", "db", "mp", "ub", "rei", "entnahmeort", "ph", "pg", "pk", 
		"zb", "pzb", "pkom", "sdm", "pnh", "mm", "mw", "mess_kommentar", "pn", 
		"ursprungsort", "ursprungsort_header", "ursprungsortdaten", "uh", "ug", 
		"uk", "version", "uebertragungsformat", "datenbasis", "datenbasis_s", 
		"netzkennung", "erzeuger", "staat_der_messstelle_lang", "staat_der_messstelle_kurz", 
		"staat_der_messstelle_s", "messstelle", "messlabor", "probe_id", "messungs_id", 
		"proben_nr", "hauptprobennummer", "nebenprobennummer", "messprogramm_c", 
		"messprogramm_s", "messprogramm_land", "probenahmeinstitution", "probenart", 
		"zeitbasis", "zeitbasis_s", "soll_datum_uhrzeit_a", "soll_datum_uhrzeit_e", 
		"ursprungs_datum_uhrzeit", "probenahme_datum_uhrzeit_a", "probenahme_datum_uhrzeit_e", 
		"umweltbereich_c", "umweltbereich_s", "deskriptoren", "rei_programmpunkt", 
		"rei_programmpunktgruppe", "referenz_datum_uhrzeit", "testdaten", "szenario", 
		"sek_datenbasis", "sek_datenbasis_s", "u_herkunftsland_lang", "u_herkunftsland_kurz", 
		"u_herkunftsland_s", "u_gemeindeschluessel", "u_gemeindename", "u_orts_zusatzkennzahl", 
		"u_koordinaten", "u_koordinaten_s", "u_orts_zusatzcode", "u_orts_zusatztext", 
		"u_nuts_code", "p_herkunftsland_lang", "p_herkunftsland_kurz", "p_herkunftsland_s", 
		"p_gemeindeschluessel", "p_gemeindename", "p_orts_zusatzkennzahl", "p_koordinaten", 
		"p_koordinaten_s", "p_orts_zusatzcode", "p_orts_zusatztext", "p_nuts_code", 
		"p_site_id", "p_site_name", "p_hoehe_nn", "p_hoehe_land", "mehrzweckfeld", 
		"mess_datum_uhrzeit", "messzeit_sekunden", "messmethode_c", "messmethode_s", 
		"bearbeitungsstatus", "pep_flag", "erfassung_abgeschlossen", "probenzusatzbeschreibung", 
		"pzb_s", "messwert", "messwert_s", "messwert_i", "messwert_g", "messwert_nwg", 
		"messwert_nwg_s", "messwert_nwg_i", "messwert_nwg_g", "kommentar", "kommentar_t", 
		"probenkommentar", "probenkommentar_t"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'%ENDE%'", "'%PROBE%'", "'%MESSUNG%'", "'%URSPRUNGSORT%'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, "VERSION", "UEBERTRAGUNGSFORMAT", "DATENBASIS", 
		"DATENBASIS_S", "NETZKENNUNG", "ERZEUGER", "STAAT_DER_MESSSTELLE_LANG", 
		"STAAT_DER_MESSSTELLE_KURZ", "STAAT_DER_MESSSTELLE_S", "MESSSTELLE", "MESSLABOR", 
		"PROBE_ID", "MESSUNGS_ID", "PROBEN_NR", "HAUPTPROBENNUMMER", "NEBENPROBENNUMMER", 
		"MESSPROGRAMM_C", "MESSPROGRAMM_S", "MESSPROGRAMM_LAND", "PROBENAHMEINSTITUTION", 
		"PROBENART", "ZEITBASIS", "ZEITBASIS_S", "SOLL_DATUM_UHRZEIT_A", "SOLL_DATUM_UHRZEIT_E", 
		"URSPRUNGS_DATUM_UHRZEIT", "PROBENAHME_DATUM_UHRZEIT_A", "PROBENAHME_DATUM_UHRZEIT_E", 
		"UMWELTBEREICH_C", "UMWELTBEREICH_S", "DESKRIPTOREN", "REI_PROGRAMMPUNKT", 
		"REI_PROGRAMMPUNKTGRUPPE", "REFERENZ_DATUM_UHRZEIT", "TESTDATEN", "SZENARIO", 
		"SEK_DATENBASIS", "SEK_DATENBASIS_S", "U_HERKUNFTSLAND_LANG", "U_HERKUNFTSLAND_KURZ", 
		"U_HERKUNFTSLAND_S", "U_GEMEINDESCHLUESSEL", "U_GEMEINDENAME", "U_ORTS_ZUSATZKENNZAHL", 
		"U_KOORDINATEN", "U_KOORDINATEN_S", "U_ORTS_ZUSATZCODE", "U_ORTS_ZUSATZTEXT", 
		"U_NUTS_CODE", "P_HERKUNFTSLAND_LANG", "P_HERKUNFTSLAND_KURZ", "P_HERKUNFTSLAND_S", 
		"P_GEMEINDESCHLUESSEL", "P_GEMEINDENAME", "P_ORTS_ZUSATZKENNZAHL", "P_KOORDINATEN", 
		"P_KOORDINATEN_S", "P_ORTS_ZUSATZCODE", "P_ORTS_ZUSATZTEXT", "P_NUTS_CODE", 
		"P_SITE_ID", "P_SITE_NAME", "P_HOEHE_NN", "P_HOEHE_LAND", "MEHRZWECKFELD", 
		"MESS_DATUM_UHRZEIT", "MESSZEIT_SEKUNDEN", "MESSMETHODE_C", "MESSMETHODE_S", 
		"BEARBEITUNGSSTATUS", "PEP_FLAG", "ERFASSUNG_ABGESCHLOSSEN", "PROBENZUSATZBESCHREIBUNG", 
		"PZB_S", "MESSWERT", "MESSWERT_S", "MESSWERT_I", "MESSWERT_G", "MESSWERT_NWG", 
		"MESSWERT_NWG_S", "MESSWERT_NWG_I", "MESSWERT_NWG_G", "KOMMENTAR", "KOMMENTAR_T", 
		"PROBENKOMMENTAR", "PROBENKOMMENTAR_T", "WS", "NEWLINE", "STRING_ESC", 
		"STRING", "C"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Laf.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LafParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProbendateiContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(LafParser.EOF, 0); }
		public List<ProbeContext> probe() {
			return getRuleContexts(ProbeContext.class);
		}
		public ProbeContext probe(int i) {
			return getRuleContext(ProbeContext.class,i);
		}
		public EndContext end() {
			return getRuleContext(EndContext.class,0);
		}
		public ProbendateiContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probendatei; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbendatei(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbendatei(this);
		}
	}

	public final ProbendateiContext probendatei() throws RecognitionException {
		ProbendateiContext _localctx = new ProbendateiContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_probendatei);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(232);
				probe();
				}
				}
				setState(237);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(239);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(238);
				end();
				}
			}

			setState(241);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EndContext extends ParserRuleContext {
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public EndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_end; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterEnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitEnd(this);
		}
	}

	public final EndContext end() throws RecognitionException {
		EndContext _localctx = new EndContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_end);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			match(T__0);
			setState(245);
			_la = _input.LA(1);
			if (_la==NEWLINE) {
				{
				setState(244);
				match(NEWLINE);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProbeContext extends ParserRuleContext {
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<ProbedatenContext> probedaten() {
			return getRuleContexts(ProbedatenContext.class);
		}
		public ProbedatenContext probedaten(int i) {
			return getRuleContext(ProbedatenContext.class,i);
		}
		public ProbeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probe; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbe(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbe(this);
		}
	}

	public final ProbeContext probe() throws RecognitionException {
		ProbeContext _localctx = new ProbeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_probe);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			match(T__1);
			setState(248);
			match(NEWLINE);
			setState(250); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(249);
				probedaten();
				}
				}
				setState(252); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << VERSION) | (1L << UEBERTRAGUNGSFORMAT) | (1L << DATENBASIS) | (1L << DATENBASIS_S) | (1L << NETZKENNUNG) | (1L << ERZEUGER) | (1L << STAAT_DER_MESSSTELLE_LANG) | (1L << STAAT_DER_MESSSTELLE_KURZ) | (1L << STAAT_DER_MESSSTELLE_S) | (1L << MESSSTELLE) | (1L << MESSLABOR) | (1L << PROBE_ID) | (1L << MESSUNGS_ID) | (1L << PROBEN_NR) | (1L << HAUPTPROBENNUMMER) | (1L << NEBENPROBENNUMMER) | (1L << MESSPROGRAMM_C) | (1L << MESSPROGRAMM_S) | (1L << MESSPROGRAMM_LAND) | (1L << PROBENAHMEINSTITUTION) | (1L << PROBENART) | (1L << ZEITBASIS) | (1L << ZEITBASIS_S) | (1L << SOLL_DATUM_UHRZEIT_A) | (1L << SOLL_DATUM_UHRZEIT_E) | (1L << PROBENAHME_DATUM_UHRZEIT_A) | (1L << PROBENAHME_DATUM_UHRZEIT_E) | (1L << UMWELTBEREICH_C) | (1L << UMWELTBEREICH_S) | (1L << DESKRIPTOREN) | (1L << REI_PROGRAMMPUNKT) | (1L << REI_PROGRAMMPUNKTGRUPPE) | (1L << REFERENZ_DATUM_UHRZEIT) | (1L << TESTDATEN) | (1L << SZENARIO) | (1L << SEK_DATENBASIS) | (1L << SEK_DATENBASIS_S) | (1L << U_HERKUNFTSLAND_LANG) | (1L << U_HERKUNFTSLAND_KURZ) | (1L << U_HERKUNFTSLAND_S) | (1L << U_GEMEINDESCHLUESSEL) | (1L << U_GEMEINDENAME) | (1L << U_ORTS_ZUSATZKENNZAHL) | (1L << U_KOORDINATEN) | (1L << U_KOORDINATEN_S) | (1L << U_ORTS_ZUSATZCODE) | (1L << U_ORTS_ZUSATZTEXT) | (1L << U_NUTS_CODE) | (1L << P_HERKUNFTSLAND_LANG) | (1L << P_HERKUNFTSLAND_KURZ) | (1L << P_HERKUNFTSLAND_S) | (1L << P_GEMEINDESCHLUESSEL) | (1L << P_GEMEINDENAME) | (1L << P_ORTS_ZUSATZKENNZAHL) | (1L << P_KOORDINATEN) | (1L << P_KOORDINATEN_S) | (1L << P_ORTS_ZUSATZCODE) | (1L << P_ORTS_ZUSATZTEXT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (P_NUTS_CODE - 64)) | (1L << (P_SITE_ID - 64)) | (1L << (P_SITE_NAME - 64)) | (1L << (P_HOEHE_NN - 64)) | (1L << (P_HOEHE_LAND - 64)) | (1L << (MEHRZWECKFELD - 64)) | (1L << (MESS_DATUM_UHRZEIT - 64)) | (1L << (MESSZEIT_SEKUNDEN - 64)) | (1L << (MESSMETHODE_C - 64)) | (1L << (MESSMETHODE_S - 64)) | (1L << (BEARBEITUNGSSTATUS - 64)) | (1L << (PEP_FLAG - 64)) | (1L << (ERFASSUNG_ABGESCHLOSSEN - 64)) | (1L << (PROBENZUSATZBESCHREIBUNG - 64)) | (1L << (PZB_S - 64)) | (1L << (MESSWERT - 64)) | (1L << (MESSWERT_S - 64)) | (1L << (MESSWERT_I - 64)) | (1L << (MESSWERT_G - 64)) | (1L << (MESSWERT_NWG - 64)) | (1L << (MESSWERT_NWG_S - 64)) | (1L << (MESSWERT_NWG_I - 64)) | (1L << (MESSWERT_NWG_G - 64)) | (1L << (KOMMENTAR - 64)) | (1L << (KOMMENTAR_T - 64)) | (1L << (PROBENKOMMENTAR - 64)) | (1L << (PROBENKOMMENTAR_T - 64)))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProbedatenContext extends ParserRuleContext {
		public DbContext db() {
			return getRuleContext(DbContext.class,0);
		}
		public VersionContext version() {
			return getRuleContext(VersionContext.class,0);
		}
		public UebertragungsformatContext uebertragungsformat() {
			return getRuleContext(UebertragungsformatContext.class,0);
		}
		public NetzkennungContext netzkennung() {
			return getRuleContext(NetzkennungContext.class,0);
		}
		public ErzeugerContext erzeuger() {
			return getRuleContext(ErzeugerContext.class,0);
		}
		public SdmContext sdm() {
			return getRuleContext(SdmContext.class,0);
		}
		public MessstelleContext messstelle() {
			return getRuleContext(MessstelleContext.class,0);
		}
		public MesslaborContext messlabor() {
			return getRuleContext(MesslaborContext.class,0);
		}
		public Probe_idContext probe_id() {
			return getRuleContext(Probe_idContext.class,0);
		}
		public PnhContext pnh() {
			return getRuleContext(PnhContext.class,0);
		}
		public MpContext mp() {
			return getRuleContext(MpContext.class,0);
		}
		public Messprogramm_landContext messprogramm_land() {
			return getRuleContext(Messprogramm_landContext.class,0);
		}
		public ProbenahmeinstitutionContext probenahmeinstitution() {
			return getRuleContext(ProbenahmeinstitutionContext.class,0);
		}
		public ProbenartContext probenart() {
			return getRuleContext(ProbenartContext.class,0);
		}
		public ZbContext zb() {
			return getRuleContext(ZbContext.class,0);
		}
		public Probenahme_datum_uhrzeit_aContext probenahme_datum_uhrzeit_a() {
			return getRuleContext(Probenahme_datum_uhrzeit_aContext.class,0);
		}
		public Probenahme_datum_uhrzeit_eContext probenahme_datum_uhrzeit_e() {
			return getRuleContext(Probenahme_datum_uhrzeit_eContext.class,0);
		}
		public Soll_datum_uhrzeit_aContext soll_datum_uhrzeit_a() {
			return getRuleContext(Soll_datum_uhrzeit_aContext.class,0);
		}
		public Soll_datum_uhrzeit_eContext soll_datum_uhrzeit_e() {
			return getRuleContext(Soll_datum_uhrzeit_eContext.class,0);
		}
		public UbContext ub() {
			return getRuleContext(UbContext.class,0);
		}
		public DeskriptorenContext deskriptoren() {
			return getRuleContext(DeskriptorenContext.class,0);
		}
		public ReiContext rei() {
			return getRuleContext(ReiContext.class,0);
		}
		public Referenz_datum_uhrzeitContext referenz_datum_uhrzeit() {
			return getRuleContext(Referenz_datum_uhrzeitContext.class,0);
		}
		public TestdatenContext testdaten() {
			return getRuleContext(TestdatenContext.class,0);
		}
		public SzenarioContext szenario() {
			return getRuleContext(SzenarioContext.class,0);
		}
		public Sek_datenbasisContext sek_datenbasis() {
			return getRuleContext(Sek_datenbasisContext.class,0);
		}
		public Sek_datenbasis_sContext sek_datenbasis_s() {
			return getRuleContext(Sek_datenbasis_sContext.class,0);
		}
		public UrsprungsortContext ursprungsort() {
			return getRuleContext(UrsprungsortContext.class,0);
		}
		public EntnahmeortContext entnahmeort() {
			return getRuleContext(EntnahmeortContext.class,0);
		}
		public MehrzweckfeldContext mehrzweckfeld() {
			return getRuleContext(MehrzweckfeldContext.class,0);
		}
		public MessungContext messung() {
			return getRuleContext(MessungContext.class,0);
		}
		public PzbContext pzb() {
			return getRuleContext(PzbContext.class,0);
		}
		public PkomContext pkom() {
			return getRuleContext(PkomContext.class,0);
		}
		public ProbedatenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probedaten; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbedaten(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbedaten(this);
		}
	}

	public final ProbedatenContext probedaten() throws RecognitionException {
		ProbedatenContext _localctx = new ProbedatenContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_probedaten);
		try {
			setState(287);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(254);
				db();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(255);
				version();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(256);
				uebertragungsformat();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(257);
				netzkennung();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(258);
				erzeuger();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(259);
				sdm();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(260);
				messstelle();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(261);
				messlabor();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(262);
				probe_id();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(263);
				pnh();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(264);
				mp();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(265);
				messprogramm_land();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(266);
				probenahmeinstitution();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(267);
				probenart();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(268);
				zb();
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(269);
				probenahme_datum_uhrzeit_a();
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(270);
				probenahme_datum_uhrzeit_e();
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(271);
				soll_datum_uhrzeit_a();
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(272);
				soll_datum_uhrzeit_e();
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(273);
				ub();
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(274);
				deskriptoren();
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(275);
				rei();
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(276);
				referenz_datum_uhrzeit();
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(277);
				testdaten();
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(278);
				szenario();
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				setState(279);
				sek_datenbasis();
				}
				break;
			case 27:
				enterOuterAlt(_localctx, 27);
				{
				setState(280);
				sek_datenbasis_s();
				}
				break;
			case 28:
				enterOuterAlt(_localctx, 28);
				{
				setState(281);
				ursprungsort();
				}
				break;
			case 29:
				enterOuterAlt(_localctx, 29);
				{
				setState(282);
				entnahmeort();
				}
				break;
			case 30:
				enterOuterAlt(_localctx, 30);
				{
				setState(283);
				mehrzweckfeld();
				}
				break;
			case 31:
				enterOuterAlt(_localctx, 31);
				{
				setState(284);
				messung();
				}
				break;
			case 32:
				enterOuterAlt(_localctx, 32);
				{
				setState(285);
				pzb();
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 33);
				{
				setState(286);
				pkom();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MessungContext extends ParserRuleContext {
		public Mess_headerContext mess_header() {
			return getRuleContext(Mess_headerContext.class,0);
		}
		public List<MessungdatenContext> messungdaten() {
			return getRuleContexts(MessungdatenContext.class);
		}
		public MessungdatenContext messungdaten(int i) {
			return getRuleContext(MessungdatenContext.class,i);
		}
		public MessungContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messung; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMessung(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMessung(this);
		}
	}

	public final MessungContext messung() throws RecognitionException {
		MessungContext _localctx = new MessungContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_messung);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(289);
				mess_header();
				}
			}

			setState(293); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(292);
					messungdaten();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(295); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Mess_headerContext extends ParserRuleContext {
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public Mess_headerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mess_header; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMess_header(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMess_header(this);
		}
	}

	public final Mess_headerContext mess_header() throws RecognitionException {
		Mess_headerContext _localctx = new Mess_headerContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_mess_header);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(297);
			match(T__2);
			setState(298);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MessungdatenContext extends ParserRuleContext {
		public Messungs_idContext messungs_id() {
			return getRuleContext(Messungs_idContext.class,0);
		}
		public PnContext pn() {
			return getRuleContext(PnContext.class,0);
		}
		public Mess_datum_uhrzeitContext mess_datum_uhrzeit() {
			return getRuleContext(Mess_datum_uhrzeitContext.class,0);
		}
		public Messzeit_sekundenContext messzeit_sekunden() {
			return getRuleContext(Messzeit_sekundenContext.class,0);
		}
		public MmContext mm() {
			return getRuleContext(MmContext.class,0);
		}
		public BearbeitungsstatusContext bearbeitungsstatus() {
			return getRuleContext(BearbeitungsstatusContext.class,0);
		}
		public Pep_flagContext pep_flag() {
			return getRuleContext(Pep_flagContext.class,0);
		}
		public Erfassung_abgeschlossenContext erfassung_abgeschlossen() {
			return getRuleContext(Erfassung_abgeschlossenContext.class,0);
		}
		public MwContext mw() {
			return getRuleContext(MwContext.class,0);
		}
		public Mess_kommentarContext mess_kommentar() {
			return getRuleContext(Mess_kommentarContext.class,0);
		}
		public MessungdatenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messungdaten; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMessungdaten(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMessungdaten(this);
		}
	}

	public final MessungdatenContext messungdaten() throws RecognitionException {
		MessungdatenContext _localctx = new MessungdatenContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_messungdaten);
		try {
			setState(310);
			switch (_input.LA(1)) {
			case MESSUNGS_ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(300);
				messungs_id();
				}
				break;
			case PROBEN_NR:
			case NEBENPROBENNUMMER:
				enterOuterAlt(_localctx, 2);
				{
				setState(301);
				pn();
				}
				break;
			case MESS_DATUM_UHRZEIT:
				enterOuterAlt(_localctx, 3);
				{
				setState(302);
				mess_datum_uhrzeit();
				}
				break;
			case MESSZEIT_SEKUNDEN:
				enterOuterAlt(_localctx, 4);
				{
				setState(303);
				messzeit_sekunden();
				}
				break;
			case MESSMETHODE_C:
			case MESSMETHODE_S:
				enterOuterAlt(_localctx, 5);
				{
				setState(304);
				mm();
				}
				break;
			case BEARBEITUNGSSTATUS:
				enterOuterAlt(_localctx, 6);
				{
				setState(305);
				bearbeitungsstatus();
				}
				break;
			case PEP_FLAG:
				enterOuterAlt(_localctx, 7);
				{
				setState(306);
				pep_flag();
				}
				break;
			case ERFASSUNG_ABGESCHLOSSEN:
				enterOuterAlt(_localctx, 8);
				{
				setState(307);
				erfassung_abgeschlossen();
				}
				break;
			case MESSWERT:
			case MESSWERT_S:
			case MESSWERT_I:
			case MESSWERT_G:
			case MESSWERT_NWG:
			case MESSWERT_NWG_S:
			case MESSWERT_NWG_I:
			case MESSWERT_NWG_G:
				enterOuterAlt(_localctx, 9);
				{
				setState(308);
				mw();
				}
				break;
			case KOMMENTAR:
			case KOMMENTAR_T:
				enterOuterAlt(_localctx, 10);
				{
				setState(309);
				mess_kommentar();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DbContext extends ParserRuleContext {
		public DatenbasisContext datenbasis() {
			return getRuleContext(DatenbasisContext.class,0);
		}
		public Datenbasis_sContext datenbasis_s() {
			return getRuleContext(Datenbasis_sContext.class,0);
		}
		public DbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterDb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitDb(this);
		}
	}

	public final DbContext db() throws RecognitionException {
		DbContext _localctx = new DbContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_db);
		try {
			setState(314);
			switch (_input.LA(1)) {
			case DATENBASIS:
				enterOuterAlt(_localctx, 1);
				{
				setState(312);
				datenbasis();
				}
				break;
			case DATENBASIS_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(313);
				datenbasis_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MpContext extends ParserRuleContext {
		public Messprogramm_cContext messprogramm_c() {
			return getRuleContext(Messprogramm_cContext.class,0);
		}
		public Messprogramm_sContext messprogramm_s() {
			return getRuleContext(Messprogramm_sContext.class,0);
		}
		public MpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMp(this);
		}
	}

	public final MpContext mp() throws RecognitionException {
		MpContext _localctx = new MpContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_mp);
		try {
			setState(318);
			switch (_input.LA(1)) {
			case MESSPROGRAMM_C:
				enterOuterAlt(_localctx, 1);
				{
				setState(316);
				messprogramm_c();
				}
				break;
			case MESSPROGRAMM_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(317);
				messprogramm_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UbContext extends ParserRuleContext {
		public Umweltbereich_cContext umweltbereich_c() {
			return getRuleContext(Umweltbereich_cContext.class,0);
		}
		public Umweltbereich_sContext umweltbereich_s() {
			return getRuleContext(Umweltbereich_sContext.class,0);
		}
		public UbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ub; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUb(this);
		}
	}

	public final UbContext ub() throws RecognitionException {
		UbContext _localctx = new UbContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_ub);
		try {
			setState(322);
			switch (_input.LA(1)) {
			case UMWELTBEREICH_C:
				enterOuterAlt(_localctx, 1);
				{
				setState(320);
				umweltbereich_c();
				}
				break;
			case UMWELTBEREICH_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(321);
				umweltbereich_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReiContext extends ParserRuleContext {
		public Rei_programmpunktContext rei_programmpunkt() {
			return getRuleContext(Rei_programmpunktContext.class,0);
		}
		public Rei_programmpunktgruppeContext rei_programmpunktgruppe() {
			return getRuleContext(Rei_programmpunktgruppeContext.class,0);
		}
		public ReiContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rei; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterRei(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitRei(this);
		}
	}

	public final ReiContext rei() throws RecognitionException {
		ReiContext _localctx = new ReiContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_rei);
		try {
			setState(326);
			switch (_input.LA(1)) {
			case REI_PROGRAMMPUNKT:
				enterOuterAlt(_localctx, 1);
				{
				setState(324);
				rei_programmpunkt();
				}
				break;
			case REI_PROGRAMMPUNKTGRUPPE:
				enterOuterAlt(_localctx, 2);
				{
				setState(325);
				rei_programmpunktgruppe();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EntnahmeortContext extends ParserRuleContext {
		public PhContext ph() {
			return getRuleContext(PhContext.class,0);
		}
		public PgContext pg() {
			return getRuleContext(PgContext.class,0);
		}
		public P_orts_zusatzkennzahlContext p_orts_zusatzkennzahl() {
			return getRuleContext(P_orts_zusatzkennzahlContext.class,0);
		}
		public PkContext pk() {
			return getRuleContext(PkContext.class,0);
		}
		public P_orts_zusatzcodeContext p_orts_zusatzcode() {
			return getRuleContext(P_orts_zusatzcodeContext.class,0);
		}
		public P_orts_zusatztextContext p_orts_zusatztext() {
			return getRuleContext(P_orts_zusatztextContext.class,0);
		}
		public P_nuts_codeContext p_nuts_code() {
			return getRuleContext(P_nuts_codeContext.class,0);
		}
		public P_site_idContext p_site_id() {
			return getRuleContext(P_site_idContext.class,0);
		}
		public P_site_nameContext p_site_name() {
			return getRuleContext(P_site_nameContext.class,0);
		}
		public P_hoehe_nnContext p_hoehe_nn() {
			return getRuleContext(P_hoehe_nnContext.class,0);
		}
		public P_hoehe_landContext p_hoehe_land() {
			return getRuleContext(P_hoehe_landContext.class,0);
		}
		public EntnahmeortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entnahmeort; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterEntnahmeort(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitEntnahmeort(this);
		}
	}

	public final EntnahmeortContext entnahmeort() throws RecognitionException {
		EntnahmeortContext _localctx = new EntnahmeortContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_entnahmeort);
		try {
			setState(339);
			switch (_input.LA(1)) {
			case P_HERKUNFTSLAND_LANG:
			case P_HERKUNFTSLAND_KURZ:
			case P_HERKUNFTSLAND_S:
				enterOuterAlt(_localctx, 1);
				{
				setState(328);
				ph();
				}
				break;
			case P_GEMEINDESCHLUESSEL:
			case P_GEMEINDENAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(329);
				pg();
				}
				break;
			case P_ORTS_ZUSATZKENNZAHL:
				enterOuterAlt(_localctx, 3);
				{
				setState(330);
				p_orts_zusatzkennzahl();
				}
				break;
			case P_KOORDINATEN:
			case P_KOORDINATEN_S:
				enterOuterAlt(_localctx, 4);
				{
				setState(331);
				pk();
				}
				break;
			case P_ORTS_ZUSATZCODE:
				enterOuterAlt(_localctx, 5);
				{
				setState(332);
				p_orts_zusatzcode();
				}
				break;
			case P_ORTS_ZUSATZTEXT:
				enterOuterAlt(_localctx, 6);
				{
				setState(333);
				p_orts_zusatztext();
				}
				break;
			case P_NUTS_CODE:
				enterOuterAlt(_localctx, 7);
				{
				setState(334);
				p_nuts_code();
				}
				break;
			case P_SITE_ID:
				enterOuterAlt(_localctx, 8);
				{
				setState(335);
				p_site_id();
				}
				break;
			case P_SITE_NAME:
				enterOuterAlt(_localctx, 9);
				{
				setState(336);
				p_site_name();
				}
				break;
			case P_HOEHE_NN:
				enterOuterAlt(_localctx, 10);
				{
				setState(337);
				p_hoehe_nn();
				}
				break;
			case P_HOEHE_LAND:
				enterOuterAlt(_localctx, 11);
				{
				setState(338);
				p_hoehe_land();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PhContext extends ParserRuleContext {
		public P_herkunftsland_langContext p_herkunftsland_lang() {
			return getRuleContext(P_herkunftsland_langContext.class,0);
		}
		public P_herkunftsland_kurzContext p_herkunftsland_kurz() {
			return getRuleContext(P_herkunftsland_kurzContext.class,0);
		}
		public P_herkunftsland_sContext p_herkunftsland_s() {
			return getRuleContext(P_herkunftsland_sContext.class,0);
		}
		public PhContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ph; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterPh(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitPh(this);
		}
	}

	public final PhContext ph() throws RecognitionException {
		PhContext _localctx = new PhContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_ph);
		try {
			setState(344);
			switch (_input.LA(1)) {
			case P_HERKUNFTSLAND_LANG:
				enterOuterAlt(_localctx, 1);
				{
				setState(341);
				p_herkunftsland_lang();
				}
				break;
			case P_HERKUNFTSLAND_KURZ:
				enterOuterAlt(_localctx, 2);
				{
				setState(342);
				p_herkunftsland_kurz();
				}
				break;
			case P_HERKUNFTSLAND_S:
				enterOuterAlt(_localctx, 3);
				{
				setState(343);
				p_herkunftsland_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PgContext extends ParserRuleContext {
		public P_gemeindeschluesselContext p_gemeindeschluessel() {
			return getRuleContext(P_gemeindeschluesselContext.class,0);
		}
		public P_gemeindenameContext p_gemeindename() {
			return getRuleContext(P_gemeindenameContext.class,0);
		}
		public PgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterPg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitPg(this);
		}
	}

	public final PgContext pg() throws RecognitionException {
		PgContext _localctx = new PgContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_pg);
		try {
			setState(348);
			switch (_input.LA(1)) {
			case P_GEMEINDESCHLUESSEL:
				enterOuterAlt(_localctx, 1);
				{
				setState(346);
				p_gemeindeschluessel();
				}
				break;
			case P_GEMEINDENAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(347);
				p_gemeindename();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PkContext extends ParserRuleContext {
		public P_koordinatenContext p_koordinaten() {
			return getRuleContext(P_koordinatenContext.class,0);
		}
		public P_koordinaten_sContext p_koordinaten_s() {
			return getRuleContext(P_koordinaten_sContext.class,0);
		}
		public PkContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pk; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterPk(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitPk(this);
		}
	}

	public final PkContext pk() throws RecognitionException {
		PkContext _localctx = new PkContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_pk);
		try {
			setState(352);
			switch (_input.LA(1)) {
			case P_KOORDINATEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(350);
				p_koordinaten();
				}
				break;
			case P_KOORDINATEN_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(351);
				p_koordinaten_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ZbContext extends ParserRuleContext {
		public ZeitbasisContext zeitbasis() {
			return getRuleContext(ZeitbasisContext.class,0);
		}
		public Zeitbasis_sContext zeitbasis_s() {
			return getRuleContext(Zeitbasis_sContext.class,0);
		}
		public ZbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_zb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterZb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitZb(this);
		}
	}

	public final ZbContext zb() throws RecognitionException {
		ZbContext _localctx = new ZbContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_zb);
		try {
			setState(356);
			switch (_input.LA(1)) {
			case ZEITBASIS:
				enterOuterAlt(_localctx, 1);
				{
				setState(354);
				zeitbasis();
				}
				break;
			case ZEITBASIS_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(355);
				zeitbasis_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PzbContext extends ParserRuleContext {
		public ProbenzusatzbeschreibungContext probenzusatzbeschreibung() {
			return getRuleContext(ProbenzusatzbeschreibungContext.class,0);
		}
		public Pzb_sContext pzb_s() {
			return getRuleContext(Pzb_sContext.class,0);
		}
		public PzbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pzb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterPzb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitPzb(this);
		}
	}

	public final PzbContext pzb() throws RecognitionException {
		PzbContext _localctx = new PzbContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_pzb);
		try {
			setState(360);
			switch (_input.LA(1)) {
			case PROBENZUSATZBESCHREIBUNG:
				enterOuterAlt(_localctx, 1);
				{
				setState(358);
				probenzusatzbeschreibung();
				}
				break;
			case PZB_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(359);
				pzb_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PkomContext extends ParserRuleContext {
		public ProbenkommentarContext probenkommentar() {
			return getRuleContext(ProbenkommentarContext.class,0);
		}
		public Probenkommentar_tContext probenkommentar_t() {
			return getRuleContext(Probenkommentar_tContext.class,0);
		}
		public PkomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pkom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterPkom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitPkom(this);
		}
	}

	public final PkomContext pkom() throws RecognitionException {
		PkomContext _localctx = new PkomContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_pkom);
		try {
			setState(364);
			switch (_input.LA(1)) {
			case PROBENKOMMENTAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(362);
				probenkommentar();
				}
				break;
			case PROBENKOMMENTAR_T:
				enterOuterAlt(_localctx, 2);
				{
				setState(363);
				probenkommentar_t();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SdmContext extends ParserRuleContext {
		public Staat_der_messstelle_langContext staat_der_messstelle_lang() {
			return getRuleContext(Staat_der_messstelle_langContext.class,0);
		}
		public Staat_der_messstelle_kurzContext staat_der_messstelle_kurz() {
			return getRuleContext(Staat_der_messstelle_kurzContext.class,0);
		}
		public Staat_der_messstelle_sContext staat_der_messstelle_s() {
			return getRuleContext(Staat_der_messstelle_sContext.class,0);
		}
		public SdmContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sdm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterSdm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitSdm(this);
		}
	}

	public final SdmContext sdm() throws RecognitionException {
		SdmContext _localctx = new SdmContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_sdm);
		try {
			setState(369);
			switch (_input.LA(1)) {
			case STAAT_DER_MESSSTELLE_LANG:
				enterOuterAlt(_localctx, 1);
				{
				setState(366);
				staat_der_messstelle_lang();
				}
				break;
			case STAAT_DER_MESSSTELLE_KURZ:
				enterOuterAlt(_localctx, 2);
				{
				setState(367);
				staat_der_messstelle_kurz();
				}
				break;
			case STAAT_DER_MESSSTELLE_S:
				enterOuterAlt(_localctx, 3);
				{
				setState(368);
				staat_der_messstelle_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PnhContext extends ParserRuleContext {
		public Proben_nrContext proben_nr() {
			return getRuleContext(Proben_nrContext.class,0);
		}
		public HauptprobennummerContext hauptprobennummer() {
			return getRuleContext(HauptprobennummerContext.class,0);
		}
		public PnhContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pnh; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterPnh(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitPnh(this);
		}
	}

	public final PnhContext pnh() throws RecognitionException {
		PnhContext _localctx = new PnhContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_pnh);
		try {
			setState(373);
			switch (_input.LA(1)) {
			case PROBEN_NR:
				enterOuterAlt(_localctx, 1);
				{
				setState(371);
				proben_nr();
				}
				break;
			case HAUPTPROBENNUMMER:
				enterOuterAlt(_localctx, 2);
				{
				setState(372);
				hauptprobennummer();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MmContext extends ParserRuleContext {
		public Messmethode_cContext messmethode_c() {
			return getRuleContext(Messmethode_cContext.class,0);
		}
		public Messmethode_sContext messmethode_s() {
			return getRuleContext(Messmethode_sContext.class,0);
		}
		public MmContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMm(this);
		}
	}

	public final MmContext mm() throws RecognitionException {
		MmContext _localctx = new MmContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_mm);
		try {
			setState(377);
			switch (_input.LA(1)) {
			case MESSMETHODE_C:
				enterOuterAlt(_localctx, 1);
				{
				setState(375);
				messmethode_c();
				}
				break;
			case MESSMETHODE_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(376);
				messmethode_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MwContext extends ParserRuleContext {
		public MesswertContext messwert() {
			return getRuleContext(MesswertContext.class,0);
		}
		public Messwert_sContext messwert_s() {
			return getRuleContext(Messwert_sContext.class,0);
		}
		public Messwert_iContext messwert_i() {
			return getRuleContext(Messwert_iContext.class,0);
		}
		public Messwert_gContext messwert_g() {
			return getRuleContext(Messwert_gContext.class,0);
		}
		public Messwert_nwgContext messwert_nwg() {
			return getRuleContext(Messwert_nwgContext.class,0);
		}
		public Messwert_nwg_sContext messwert_nwg_s() {
			return getRuleContext(Messwert_nwg_sContext.class,0);
		}
		public Messwert_nwg_iContext messwert_nwg_i() {
			return getRuleContext(Messwert_nwg_iContext.class,0);
		}
		public Messwert_nwg_gContext messwert_nwg_g() {
			return getRuleContext(Messwert_nwg_gContext.class,0);
		}
		public MwContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mw; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMw(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMw(this);
		}
	}

	public final MwContext mw() throws RecognitionException {
		MwContext _localctx = new MwContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_mw);
		try {
			setState(387);
			switch (_input.LA(1)) {
			case MESSWERT:
				enterOuterAlt(_localctx, 1);
				{
				setState(379);
				messwert();
				}
				break;
			case MESSWERT_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(380);
				messwert_s();
				}
				break;
			case MESSWERT_I:
				enterOuterAlt(_localctx, 3);
				{
				setState(381);
				messwert_i();
				}
				break;
			case MESSWERT_G:
				enterOuterAlt(_localctx, 4);
				{
				setState(382);
				messwert_g();
				}
				break;
			case MESSWERT_NWG:
				enterOuterAlt(_localctx, 5);
				{
				setState(383);
				messwert_nwg();
				}
				break;
			case MESSWERT_NWG_S:
				enterOuterAlt(_localctx, 6);
				{
				setState(384);
				messwert_nwg_s();
				}
				break;
			case MESSWERT_NWG_I:
				enterOuterAlt(_localctx, 7);
				{
				setState(385);
				messwert_nwg_i();
				}
				break;
			case MESSWERT_NWG_G:
				enterOuterAlt(_localctx, 8);
				{
				setState(386);
				messwert_nwg_g();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Mess_kommentarContext extends ParserRuleContext {
		public KommentarContext kommentar() {
			return getRuleContext(KommentarContext.class,0);
		}
		public Kommentar_tContext kommentar_t() {
			return getRuleContext(Kommentar_tContext.class,0);
		}
		public Mess_kommentarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mess_kommentar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMess_kommentar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMess_kommentar(this);
		}
	}

	public final Mess_kommentarContext mess_kommentar() throws RecognitionException {
		Mess_kommentarContext _localctx = new Mess_kommentarContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_mess_kommentar);
		try {
			setState(391);
			switch (_input.LA(1)) {
			case KOMMENTAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(389);
				kommentar();
				}
				break;
			case KOMMENTAR_T:
				enterOuterAlt(_localctx, 2);
				{
				setState(390);
				kommentar_t();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PnContext extends ParserRuleContext {
		public Proben_nrContext proben_nr() {
			return getRuleContext(Proben_nrContext.class,0);
		}
		public NebenprobennummerContext nebenprobennummer() {
			return getRuleContext(NebenprobennummerContext.class,0);
		}
		public PnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterPn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitPn(this);
		}
	}

	public final PnContext pn() throws RecognitionException {
		PnContext _localctx = new PnContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_pn);
		try {
			setState(395);
			switch (_input.LA(1)) {
			case PROBEN_NR:
				enterOuterAlt(_localctx, 1);
				{
				setState(393);
				proben_nr();
				}
				break;
			case NEBENPROBENNUMMER:
				enterOuterAlt(_localctx, 2);
				{
				setState(394);
				nebenprobennummer();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UrsprungsortContext extends ParserRuleContext {
		public Ursprungsort_headerContext ursprungsort_header() {
			return getRuleContext(Ursprungsort_headerContext.class,0);
		}
		public List<UrsprungsortdatenContext> ursprungsortdaten() {
			return getRuleContexts(UrsprungsortdatenContext.class);
		}
		public UrsprungsortdatenContext ursprungsortdaten(int i) {
			return getRuleContext(UrsprungsortdatenContext.class,i);
		}
		public UrsprungsortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ursprungsort; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUrsprungsort(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUrsprungsort(this);
		}
	}

	public final UrsprungsortContext ursprungsort() throws RecognitionException {
		UrsprungsortContext _localctx = new UrsprungsortContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_ursprungsort);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(403);
			switch (_input.LA(1)) {
			case T__3:
				{
				setState(397);
				ursprungsort_header();
				}
				break;
			case U_HERKUNFTSLAND_LANG:
			case U_HERKUNFTSLAND_KURZ:
			case U_HERKUNFTSLAND_S:
			case U_GEMEINDESCHLUESSEL:
			case U_GEMEINDENAME:
			case U_ORTS_ZUSATZKENNZAHL:
			case U_KOORDINATEN:
			case U_KOORDINATEN_S:
			case U_ORTS_ZUSATZCODE:
			case U_ORTS_ZUSATZTEXT:
			case U_NUTS_CODE:
				{
				setState(399); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(398);
						ursprungsortdaten();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(401); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ursprungsort_headerContext extends ParserRuleContext {
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public Ursprungsort_headerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ursprungsort_header; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUrsprungsort_header(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUrsprungsort_header(this);
		}
	}

	public final Ursprungsort_headerContext ursprungsort_header() throws RecognitionException {
		Ursprungsort_headerContext _localctx = new Ursprungsort_headerContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_ursprungsort_header);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(405);
			match(T__3);
			setState(406);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UrsprungsortdatenContext extends ParserRuleContext {
		public UhContext uh() {
			return getRuleContext(UhContext.class,0);
		}
		public UgContext ug() {
			return getRuleContext(UgContext.class,0);
		}
		public U_orts_zusatzkennzahlContext u_orts_zusatzkennzahl() {
			return getRuleContext(U_orts_zusatzkennzahlContext.class,0);
		}
		public UkContext uk() {
			return getRuleContext(UkContext.class,0);
		}
		public U_orts_zusatzcodeContext u_orts_zusatzcode() {
			return getRuleContext(U_orts_zusatzcodeContext.class,0);
		}
		public U_orts_zusatztextContext u_orts_zusatztext() {
			return getRuleContext(U_orts_zusatztextContext.class,0);
		}
		public U_nuts_codeContext u_nuts_code() {
			return getRuleContext(U_nuts_codeContext.class,0);
		}
		public UrsprungsortdatenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ursprungsortdaten; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUrsprungsortdaten(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUrsprungsortdaten(this);
		}
	}

	public final UrsprungsortdatenContext ursprungsortdaten() throws RecognitionException {
		UrsprungsortdatenContext _localctx = new UrsprungsortdatenContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_ursprungsortdaten);
		try {
			setState(415);
			switch (_input.LA(1)) {
			case U_HERKUNFTSLAND_LANG:
			case U_HERKUNFTSLAND_KURZ:
			case U_HERKUNFTSLAND_S:
				enterOuterAlt(_localctx, 1);
				{
				setState(408);
				uh();
				}
				break;
			case U_GEMEINDESCHLUESSEL:
			case U_GEMEINDENAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(409);
				ug();
				}
				break;
			case U_ORTS_ZUSATZKENNZAHL:
				enterOuterAlt(_localctx, 3);
				{
				setState(410);
				u_orts_zusatzkennzahl();
				}
				break;
			case U_KOORDINATEN:
			case U_KOORDINATEN_S:
				enterOuterAlt(_localctx, 4);
				{
				setState(411);
				uk();
				}
				break;
			case U_ORTS_ZUSATZCODE:
				enterOuterAlt(_localctx, 5);
				{
				setState(412);
				u_orts_zusatzcode();
				}
				break;
			case U_ORTS_ZUSATZTEXT:
				enterOuterAlt(_localctx, 6);
				{
				setState(413);
				u_orts_zusatztext();
				}
				break;
			case U_NUTS_CODE:
				enterOuterAlt(_localctx, 7);
				{
				setState(414);
				u_nuts_code();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UhContext extends ParserRuleContext {
		public U_herkunftsland_langContext u_herkunftsland_lang() {
			return getRuleContext(U_herkunftsland_langContext.class,0);
		}
		public U_herkunftsland_kurzContext u_herkunftsland_kurz() {
			return getRuleContext(U_herkunftsland_kurzContext.class,0);
		}
		public U_herkunftsland_sContext u_herkunftsland_s() {
			return getRuleContext(U_herkunftsland_sContext.class,0);
		}
		public UhContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uh; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUh(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUh(this);
		}
	}

	public final UhContext uh() throws RecognitionException {
		UhContext _localctx = new UhContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_uh);
		try {
			setState(420);
			switch (_input.LA(1)) {
			case U_HERKUNFTSLAND_LANG:
				enterOuterAlt(_localctx, 1);
				{
				setState(417);
				u_herkunftsland_lang();
				}
				break;
			case U_HERKUNFTSLAND_KURZ:
				enterOuterAlt(_localctx, 2);
				{
				setState(418);
				u_herkunftsland_kurz();
				}
				break;
			case U_HERKUNFTSLAND_S:
				enterOuterAlt(_localctx, 3);
				{
				setState(419);
				u_herkunftsland_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UgContext extends ParserRuleContext {
		public U_gemeindeschluesselContext u_gemeindeschluessel() {
			return getRuleContext(U_gemeindeschluesselContext.class,0);
		}
		public U_gemeindenameContext u_gemeindename() {
			return getRuleContext(U_gemeindenameContext.class,0);
		}
		public UgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ug; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUg(this);
		}
	}

	public final UgContext ug() throws RecognitionException {
		UgContext _localctx = new UgContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_ug);
		try {
			setState(424);
			switch (_input.LA(1)) {
			case U_GEMEINDESCHLUESSEL:
				enterOuterAlt(_localctx, 1);
				{
				setState(422);
				u_gemeindeschluessel();
				}
				break;
			case U_GEMEINDENAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(423);
				u_gemeindename();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UkContext extends ParserRuleContext {
		public U_koordinatenContext u_koordinaten() {
			return getRuleContext(U_koordinatenContext.class,0);
		}
		public U_koordinaten_sContext u_koordinaten_s() {
			return getRuleContext(U_koordinaten_sContext.class,0);
		}
		public UkContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uk; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUk(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUk(this);
		}
	}

	public final UkContext uk() throws RecognitionException {
		UkContext _localctx = new UkContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_uk);
		try {
			setState(428);
			switch (_input.LA(1)) {
			case U_KOORDINATEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(426);
				u_koordinaten();
				}
				break;
			case U_KOORDINATEN_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(427);
				u_koordinaten_s();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VersionContext extends ParserRuleContext {
		public TerminalNode VERSION() { return getToken(LafParser.VERSION, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public VersionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_version; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterVersion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitVersion(this);
		}
	}

	public final VersionContext version() throws RecognitionException {
		VersionContext _localctx = new VersionContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_version);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(430);
			match(VERSION);
			setState(432);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(431);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(434);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UebertragungsformatContext extends ParserRuleContext {
		public TerminalNode UEBERTRAGUNGSFORMAT() { return getToken(LafParser.UEBERTRAGUNGSFORMAT, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public UebertragungsformatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uebertragungsformat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUebertragungsformat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUebertragungsformat(this);
		}
	}

	public final UebertragungsformatContext uebertragungsformat() throws RecognitionException {
		UebertragungsformatContext _localctx = new UebertragungsformatContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_uebertragungsformat);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(436);
			match(UEBERTRAGUNGSFORMAT);
			setState(438);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(437);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(440);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DatenbasisContext extends ParserRuleContext {
		public TerminalNode DATENBASIS() { return getToken(LafParser.DATENBASIS, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public DatenbasisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datenbasis; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterDatenbasis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitDatenbasis(this);
		}
	}

	public final DatenbasisContext datenbasis() throws RecognitionException {
		DatenbasisContext _localctx = new DatenbasisContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_datenbasis);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(442);
			match(DATENBASIS);
			setState(444);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(443);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(446);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Datenbasis_sContext extends ParserRuleContext {
		public TerminalNode DATENBASIS_S() { return getToken(LafParser.DATENBASIS_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Datenbasis_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datenbasis_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterDatenbasis_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitDatenbasis_s(this);
		}
	}

	public final Datenbasis_sContext datenbasis_s() throws RecognitionException {
		Datenbasis_sContext _localctx = new Datenbasis_sContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_datenbasis_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(448);
			match(DATENBASIS_S);
			setState(450);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(449);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(452);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NetzkennungContext extends ParserRuleContext {
		public TerminalNode NETZKENNUNG() { return getToken(LafParser.NETZKENNUNG, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public NetzkennungContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_netzkennung; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterNetzkennung(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitNetzkennung(this);
		}
	}

	public final NetzkennungContext netzkennung() throws RecognitionException {
		NetzkennungContext _localctx = new NetzkennungContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_netzkennung);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(454);
			match(NETZKENNUNG);
			setState(456);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(455);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(458);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ErzeugerContext extends ParserRuleContext {
		public TerminalNode ERZEUGER() { return getToken(LafParser.ERZEUGER, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public ErzeugerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_erzeuger; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterErzeuger(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitErzeuger(this);
		}
	}

	public final ErzeugerContext erzeuger() throws RecognitionException {
		ErzeugerContext _localctx = new ErzeugerContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_erzeuger);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(460);
			match(ERZEUGER);
			setState(462);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(461);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(464);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Staat_der_messstelle_langContext extends ParserRuleContext {
		public TerminalNode STAAT_DER_MESSSTELLE_LANG() { return getToken(LafParser.STAAT_DER_MESSSTELLE_LANG, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Staat_der_messstelle_langContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_staat_der_messstelle_lang; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterStaat_der_messstelle_lang(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitStaat_der_messstelle_lang(this);
		}
	}

	public final Staat_der_messstelle_langContext staat_der_messstelle_lang() throws RecognitionException {
		Staat_der_messstelle_langContext _localctx = new Staat_der_messstelle_langContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_staat_der_messstelle_lang);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(466);
			match(STAAT_DER_MESSSTELLE_LANG);
			setState(468);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(467);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(470);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Staat_der_messstelle_kurzContext extends ParserRuleContext {
		public TerminalNode STAAT_DER_MESSSTELLE_KURZ() { return getToken(LafParser.STAAT_DER_MESSSTELLE_KURZ, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Staat_der_messstelle_kurzContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_staat_der_messstelle_kurz; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterStaat_der_messstelle_kurz(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitStaat_der_messstelle_kurz(this);
		}
	}

	public final Staat_der_messstelle_kurzContext staat_der_messstelle_kurz() throws RecognitionException {
		Staat_der_messstelle_kurzContext _localctx = new Staat_der_messstelle_kurzContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_staat_der_messstelle_kurz);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(472);
			match(STAAT_DER_MESSSTELLE_KURZ);
			setState(474);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(473);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(476);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Staat_der_messstelle_sContext extends ParserRuleContext {
		public TerminalNode STAAT_DER_MESSSTELLE_S() { return getToken(LafParser.STAAT_DER_MESSSTELLE_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Staat_der_messstelle_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_staat_der_messstelle_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterStaat_der_messstelle_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitStaat_der_messstelle_s(this);
		}
	}

	public final Staat_der_messstelle_sContext staat_der_messstelle_s() throws RecognitionException {
		Staat_der_messstelle_sContext _localctx = new Staat_der_messstelle_sContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_staat_der_messstelle_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(478);
			match(STAAT_DER_MESSSTELLE_S);
			setState(480);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(479);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(482);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MessstelleContext extends ParserRuleContext {
		public TerminalNode MESSSTELLE() { return getToken(LafParser.MESSSTELLE, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public MessstelleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messstelle; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMessstelle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMessstelle(this);
		}
	}

	public final MessstelleContext messstelle() throws RecognitionException {
		MessstelleContext _localctx = new MessstelleContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_messstelle);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(484);
			match(MESSSTELLE);
			setState(486);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(485);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(488);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MesslaborContext extends ParserRuleContext {
		public TerminalNode MESSLABOR() { return getToken(LafParser.MESSLABOR, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public MesslaborContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messlabor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMesslabor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMesslabor(this);
		}
	}

	public final MesslaborContext messlabor() throws RecognitionException {
		MesslaborContext _localctx = new MesslaborContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_messlabor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(490);
			match(MESSLABOR);
			setState(492);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(491);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(494);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Probe_idContext extends ParserRuleContext {
		public TerminalNode PROBE_ID() { return getToken(LafParser.PROBE_ID, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Probe_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probe_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbe_id(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbe_id(this);
		}
	}

	public final Probe_idContext probe_id() throws RecognitionException {
		Probe_idContext _localctx = new Probe_idContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_probe_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(496);
			match(PROBE_ID);
			setState(498);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(497);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(500);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messungs_idContext extends ParserRuleContext {
		public TerminalNode MESSUNGS_ID() { return getToken(LafParser.MESSUNGS_ID, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Messungs_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messungs_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMessungs_id(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMessungs_id(this);
		}
	}

	public final Messungs_idContext messungs_id() throws RecognitionException {
		Messungs_idContext _localctx = new Messungs_idContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_messungs_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(502);
			match(MESSUNGS_ID);
			setState(504);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(503);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(506);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Proben_nrContext extends ParserRuleContext {
		public TerminalNode PROBEN_NR() { return getToken(LafParser.PROBEN_NR, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Proben_nrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_proben_nr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProben_nr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProben_nr(this);
		}
	}

	public final Proben_nrContext proben_nr() throws RecognitionException {
		Proben_nrContext _localctx = new Proben_nrContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_proben_nr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(508);
			match(PROBEN_NR);
			setState(510);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(509);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(512);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HauptprobennummerContext extends ParserRuleContext {
		public TerminalNode HAUPTPROBENNUMMER() { return getToken(LafParser.HAUPTPROBENNUMMER, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public HauptprobennummerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hauptprobennummer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterHauptprobennummer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitHauptprobennummer(this);
		}
	}

	public final HauptprobennummerContext hauptprobennummer() throws RecognitionException {
		HauptprobennummerContext _localctx = new HauptprobennummerContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_hauptprobennummer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(514);
			match(HAUPTPROBENNUMMER);
			setState(516);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(515);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(518);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NebenprobennummerContext extends ParserRuleContext {
		public TerminalNode NEBENPROBENNUMMER() { return getToken(LafParser.NEBENPROBENNUMMER, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public NebenprobennummerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nebenprobennummer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterNebenprobennummer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitNebenprobennummer(this);
		}
	}

	public final NebenprobennummerContext nebenprobennummer() throws RecognitionException {
		NebenprobennummerContext _localctx = new NebenprobennummerContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_nebenprobennummer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(520);
			match(NEBENPROBENNUMMER);
			setState(522);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(521);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(524);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messprogramm_cContext extends ParserRuleContext {
		public TerminalNode MESSPROGRAMM_C() { return getToken(LafParser.MESSPROGRAMM_C, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Messprogramm_cContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messprogramm_c; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMessprogramm_c(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMessprogramm_c(this);
		}
	}

	public final Messprogramm_cContext messprogramm_c() throws RecognitionException {
		Messprogramm_cContext _localctx = new Messprogramm_cContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_messprogramm_c);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(526);
			match(MESSPROGRAMM_C);
			setState(528);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(527);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(530);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messprogramm_sContext extends ParserRuleContext {
		public TerminalNode MESSPROGRAMM_S() { return getToken(LafParser.MESSPROGRAMM_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Messprogramm_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messprogramm_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMessprogramm_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMessprogramm_s(this);
		}
	}

	public final Messprogramm_sContext messprogramm_s() throws RecognitionException {
		Messprogramm_sContext _localctx = new Messprogramm_sContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_messprogramm_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(532);
			match(MESSPROGRAMM_S);
			setState(534);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(533);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(536);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messprogramm_landContext extends ParserRuleContext {
		public TerminalNode MESSPROGRAMM_LAND() { return getToken(LafParser.MESSPROGRAMM_LAND, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Messprogramm_landContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messprogramm_land; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMessprogramm_land(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMessprogramm_land(this);
		}
	}

	public final Messprogramm_landContext messprogramm_land() throws RecognitionException {
		Messprogramm_landContext _localctx = new Messprogramm_landContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_messprogramm_land);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(538);
			match(MESSPROGRAMM_LAND);
			setState(540);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(539);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(542);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProbenahmeinstitutionContext extends ParserRuleContext {
		public TerminalNode PROBENAHMEINSTITUTION() { return getToken(LafParser.PROBENAHMEINSTITUTION, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public ProbenahmeinstitutionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probenahmeinstitution; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbenahmeinstitution(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbenahmeinstitution(this);
		}
	}

	public final ProbenahmeinstitutionContext probenahmeinstitution() throws RecognitionException {
		ProbenahmeinstitutionContext _localctx = new ProbenahmeinstitutionContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_probenahmeinstitution);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(544);
			match(PROBENAHMEINSTITUTION);
			setState(546);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(545);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(548);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProbenartContext extends ParserRuleContext {
		public TerminalNode PROBENART() { return getToken(LafParser.PROBENART, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public ProbenartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probenart; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbenart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbenart(this);
		}
	}

	public final ProbenartContext probenart() throws RecognitionException {
		ProbenartContext _localctx = new ProbenartContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_probenart);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(550);
			match(PROBENART);
			setState(552);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(551);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(554);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ZeitbasisContext extends ParserRuleContext {
		public TerminalNode ZEITBASIS() { return getToken(LafParser.ZEITBASIS, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public ZeitbasisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_zeitbasis; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterZeitbasis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitZeitbasis(this);
		}
	}

	public final ZeitbasisContext zeitbasis() throws RecognitionException {
		ZeitbasisContext _localctx = new ZeitbasisContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_zeitbasis);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(556);
			match(ZEITBASIS);
			setState(558);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(557);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(560);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Zeitbasis_sContext extends ParserRuleContext {
		public TerminalNode ZEITBASIS_S() { return getToken(LafParser.ZEITBASIS_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Zeitbasis_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_zeitbasis_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterZeitbasis_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitZeitbasis_s(this);
		}
	}

	public final Zeitbasis_sContext zeitbasis_s() throws RecognitionException {
		Zeitbasis_sContext _localctx = new Zeitbasis_sContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_zeitbasis_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(562);
			match(ZEITBASIS_S);
			setState(564);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(563);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(566);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Soll_datum_uhrzeit_aContext extends ParserRuleContext {
		public TerminalNode SOLL_DATUM_UHRZEIT_A() { return getToken(LafParser.SOLL_DATUM_UHRZEIT_A, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Soll_datum_uhrzeit_aContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_soll_datum_uhrzeit_a; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterSoll_datum_uhrzeit_a(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitSoll_datum_uhrzeit_a(this);
		}
	}

	public final Soll_datum_uhrzeit_aContext soll_datum_uhrzeit_a() throws RecognitionException {
		Soll_datum_uhrzeit_aContext _localctx = new Soll_datum_uhrzeit_aContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_soll_datum_uhrzeit_a);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(568);
			match(SOLL_DATUM_UHRZEIT_A);
			setState(571);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(569);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(570);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(573);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Soll_datum_uhrzeit_eContext extends ParserRuleContext {
		public TerminalNode SOLL_DATUM_UHRZEIT_E() { return getToken(LafParser.SOLL_DATUM_UHRZEIT_E, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Soll_datum_uhrzeit_eContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_soll_datum_uhrzeit_e; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterSoll_datum_uhrzeit_e(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitSoll_datum_uhrzeit_e(this);
		}
	}

	public final Soll_datum_uhrzeit_eContext soll_datum_uhrzeit_e() throws RecognitionException {
		Soll_datum_uhrzeit_eContext _localctx = new Soll_datum_uhrzeit_eContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_soll_datum_uhrzeit_e);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(575);
			match(SOLL_DATUM_UHRZEIT_E);
			setState(578);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(576);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(577);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(580);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ursprungs_datum_uhrzeitContext extends ParserRuleContext {
		public TerminalNode URSPRUNGS_DATUM_UHRZEIT() { return getToken(LafParser.URSPRUNGS_DATUM_UHRZEIT, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Ursprungs_datum_uhrzeitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ursprungs_datum_uhrzeit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUrsprungs_datum_uhrzeit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUrsprungs_datum_uhrzeit(this);
		}
	}

	public final Ursprungs_datum_uhrzeitContext ursprungs_datum_uhrzeit() throws RecognitionException {
		Ursprungs_datum_uhrzeitContext _localctx = new Ursprungs_datum_uhrzeitContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_ursprungs_datum_uhrzeit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(582);
			match(URSPRUNGS_DATUM_UHRZEIT);
			setState(585);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(583);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(584);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(587);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Probenahme_datum_uhrzeit_aContext extends ParserRuleContext {
		public TerminalNode PROBENAHME_DATUM_UHRZEIT_A() { return getToken(LafParser.PROBENAHME_DATUM_UHRZEIT_A, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Probenahme_datum_uhrzeit_aContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probenahme_datum_uhrzeit_a; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbenahme_datum_uhrzeit_a(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbenahme_datum_uhrzeit_a(this);
		}
	}

	public final Probenahme_datum_uhrzeit_aContext probenahme_datum_uhrzeit_a() throws RecognitionException {
		Probenahme_datum_uhrzeit_aContext _localctx = new Probenahme_datum_uhrzeit_aContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_probenahme_datum_uhrzeit_a);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(589);
			match(PROBENAHME_DATUM_UHRZEIT_A);
			setState(592);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(590);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(591);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(594);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Probenahme_datum_uhrzeit_eContext extends ParserRuleContext {
		public TerminalNode PROBENAHME_DATUM_UHRZEIT_E() { return getToken(LafParser.PROBENAHME_DATUM_UHRZEIT_E, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Probenahme_datum_uhrzeit_eContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probenahme_datum_uhrzeit_e; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbenahme_datum_uhrzeit_e(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbenahme_datum_uhrzeit_e(this);
		}
	}

	public final Probenahme_datum_uhrzeit_eContext probenahme_datum_uhrzeit_e() throws RecognitionException {
		Probenahme_datum_uhrzeit_eContext _localctx = new Probenahme_datum_uhrzeit_eContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_probenahme_datum_uhrzeit_e);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(596);
			match(PROBENAHME_DATUM_UHRZEIT_E);
			setState(599);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(597);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(598);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(601);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Umweltbereich_cContext extends ParserRuleContext {
		public TerminalNode UMWELTBEREICH_C() { return getToken(LafParser.UMWELTBEREICH_C, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Umweltbereich_cContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_umweltbereich_c; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUmweltbereich_c(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUmweltbereich_c(this);
		}
	}

	public final Umweltbereich_cContext umweltbereich_c() throws RecognitionException {
		Umweltbereich_cContext _localctx = new Umweltbereich_cContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_umweltbereich_c);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(603);
			match(UMWELTBEREICH_C);
			setState(605);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(604);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(607);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Umweltbereich_sContext extends ParserRuleContext {
		public TerminalNode UMWELTBEREICH_S() { return getToken(LafParser.UMWELTBEREICH_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Umweltbereich_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_umweltbereich_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUmweltbereich_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUmweltbereich_s(this);
		}
	}

	public final Umweltbereich_sContext umweltbereich_s() throws RecognitionException {
		Umweltbereich_sContext _localctx = new Umweltbereich_sContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_umweltbereich_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(609);
			match(UMWELTBEREICH_S);
			setState(611);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(610);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(613);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeskriptorenContext extends ParserRuleContext {
		public TerminalNode DESKRIPTOREN() { return getToken(LafParser.DESKRIPTOREN, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public DeskriptorenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deskriptoren; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterDeskriptoren(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitDeskriptoren(this);
		}
	}

	public final DeskriptorenContext deskriptoren() throws RecognitionException {
		DeskriptorenContext _localctx = new DeskriptorenContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_deskriptoren);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(615);
			match(DESKRIPTOREN);
			setState(617);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(616);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(619);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rei_programmpunktContext extends ParserRuleContext {
		public TerminalNode REI_PROGRAMMPUNKT() { return getToken(LafParser.REI_PROGRAMMPUNKT, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Rei_programmpunktContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rei_programmpunkt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterRei_programmpunkt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitRei_programmpunkt(this);
		}
	}

	public final Rei_programmpunktContext rei_programmpunkt() throws RecognitionException {
		Rei_programmpunktContext _localctx = new Rei_programmpunktContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_rei_programmpunkt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(621);
			match(REI_PROGRAMMPUNKT);
			setState(623);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(622);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(625);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rei_programmpunktgruppeContext extends ParserRuleContext {
		public TerminalNode REI_PROGRAMMPUNKTGRUPPE() { return getToken(LafParser.REI_PROGRAMMPUNKTGRUPPE, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Rei_programmpunktgruppeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rei_programmpunktgruppe; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterRei_programmpunktgruppe(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitRei_programmpunktgruppe(this);
		}
	}

	public final Rei_programmpunktgruppeContext rei_programmpunktgruppe() throws RecognitionException {
		Rei_programmpunktgruppeContext _localctx = new Rei_programmpunktgruppeContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_rei_programmpunktgruppe);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(627);
			match(REI_PROGRAMMPUNKTGRUPPE);
			setState(629);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(628);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(631);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Referenz_datum_uhrzeitContext extends ParserRuleContext {
		public TerminalNode REFERENZ_DATUM_UHRZEIT() { return getToken(LafParser.REFERENZ_DATUM_UHRZEIT, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Referenz_datum_uhrzeitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_referenz_datum_uhrzeit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterReferenz_datum_uhrzeit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitReferenz_datum_uhrzeit(this);
		}
	}

	public final Referenz_datum_uhrzeitContext referenz_datum_uhrzeit() throws RecognitionException {
		Referenz_datum_uhrzeitContext _localctx = new Referenz_datum_uhrzeitContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_referenz_datum_uhrzeit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(633);
			match(REFERENZ_DATUM_UHRZEIT);
			setState(636);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(634);
				match(STRING);
				setState(635);
				match(STRING);
				}
			}

			setState(638);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TestdatenContext extends ParserRuleContext {
		public TerminalNode TESTDATEN() { return getToken(LafParser.TESTDATEN, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public TestdatenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_testdaten; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterTestdaten(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitTestdaten(this);
		}
	}

	public final TestdatenContext testdaten() throws RecognitionException {
		TestdatenContext _localctx = new TestdatenContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_testdaten);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(640);
			match(TESTDATEN);
			setState(642);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(641);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(644);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SzenarioContext extends ParserRuleContext {
		public TerminalNode SZENARIO() { return getToken(LafParser.SZENARIO, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public SzenarioContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_szenario; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterSzenario(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitSzenario(this);
		}
	}

	public final SzenarioContext szenario() throws RecognitionException {
		SzenarioContext _localctx = new SzenarioContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_szenario);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(646);
			match(SZENARIO);
			setState(648);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(647);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(650);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sek_datenbasisContext extends ParserRuleContext {
		public TerminalNode SEK_DATENBASIS() { return getToken(LafParser.SEK_DATENBASIS, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Sek_datenbasisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sek_datenbasis; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterSek_datenbasis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitSek_datenbasis(this);
		}
	}

	public final Sek_datenbasisContext sek_datenbasis() throws RecognitionException {
		Sek_datenbasisContext _localctx = new Sek_datenbasisContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_sek_datenbasis);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(652);
			match(SEK_DATENBASIS);
			setState(654);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(653);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(656);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sek_datenbasis_sContext extends ParserRuleContext {
		public TerminalNode SEK_DATENBASIS_S() { return getToken(LafParser.SEK_DATENBASIS_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Sek_datenbasis_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sek_datenbasis_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterSek_datenbasis_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitSek_datenbasis_s(this);
		}
	}

	public final Sek_datenbasis_sContext sek_datenbasis_s() throws RecognitionException {
		Sek_datenbasis_sContext _localctx = new Sek_datenbasis_sContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_sek_datenbasis_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(658);
			match(SEK_DATENBASIS_S);
			setState(660);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(659);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(662);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_herkunftsland_langContext extends ParserRuleContext {
		public TerminalNode U_HERKUNFTSLAND_LANG() { return getToken(LafParser.U_HERKUNFTSLAND_LANG, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public U_herkunftsland_langContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_herkunftsland_lang; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_herkunftsland_lang(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_herkunftsland_lang(this);
		}
	}

	public final U_herkunftsland_langContext u_herkunftsland_lang() throws RecognitionException {
		U_herkunftsland_langContext _localctx = new U_herkunftsland_langContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_u_herkunftsland_lang);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(664);
			match(U_HERKUNFTSLAND_LANG);
			setState(666);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(665);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(668);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_herkunftsland_kurzContext extends ParserRuleContext {
		public TerminalNode U_HERKUNFTSLAND_KURZ() { return getToken(LafParser.U_HERKUNFTSLAND_KURZ, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public U_herkunftsland_kurzContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_herkunftsland_kurz; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_herkunftsland_kurz(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_herkunftsland_kurz(this);
		}
	}

	public final U_herkunftsland_kurzContext u_herkunftsland_kurz() throws RecognitionException {
		U_herkunftsland_kurzContext _localctx = new U_herkunftsland_kurzContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_u_herkunftsland_kurz);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(670);
			match(U_HERKUNFTSLAND_KURZ);
			setState(672);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(671);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(674);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_herkunftsland_sContext extends ParserRuleContext {
		public TerminalNode U_HERKUNFTSLAND_S() { return getToken(LafParser.U_HERKUNFTSLAND_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public U_herkunftsland_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_herkunftsland_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_herkunftsland_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_herkunftsland_s(this);
		}
	}

	public final U_herkunftsland_sContext u_herkunftsland_s() throws RecognitionException {
		U_herkunftsland_sContext _localctx = new U_herkunftsland_sContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_u_herkunftsland_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(676);
			match(U_HERKUNFTSLAND_S);
			setState(678);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(677);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(680);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_gemeindeschluesselContext extends ParserRuleContext {
		public TerminalNode U_GEMEINDESCHLUESSEL() { return getToken(LafParser.U_GEMEINDESCHLUESSEL, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public U_gemeindeschluesselContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_gemeindeschluessel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_gemeindeschluessel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_gemeindeschluessel(this);
		}
	}

	public final U_gemeindeschluesselContext u_gemeindeschluessel() throws RecognitionException {
		U_gemeindeschluesselContext _localctx = new U_gemeindeschluesselContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_u_gemeindeschluessel);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(682);
			match(U_GEMEINDESCHLUESSEL);
			setState(684);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(683);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(686);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_gemeindenameContext extends ParserRuleContext {
		public TerminalNode U_GEMEINDENAME() { return getToken(LafParser.U_GEMEINDENAME, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public U_gemeindenameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_gemeindename; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_gemeindename(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_gemeindename(this);
		}
	}

	public final U_gemeindenameContext u_gemeindename() throws RecognitionException {
		U_gemeindenameContext _localctx = new U_gemeindenameContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_u_gemeindename);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(688);
			match(U_GEMEINDENAME);
			setState(690);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(689);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(692);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_orts_zusatzkennzahlContext extends ParserRuleContext {
		public TerminalNode U_ORTS_ZUSATZKENNZAHL() { return getToken(LafParser.U_ORTS_ZUSATZKENNZAHL, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public U_orts_zusatzkennzahlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_orts_zusatzkennzahl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_orts_zusatzkennzahl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_orts_zusatzkennzahl(this);
		}
	}

	public final U_orts_zusatzkennzahlContext u_orts_zusatzkennzahl() throws RecognitionException {
		U_orts_zusatzkennzahlContext _localctx = new U_orts_zusatzkennzahlContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_u_orts_zusatzkennzahl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(694);
			match(U_ORTS_ZUSATZKENNZAHL);
			setState(696);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(695);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(698);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_koordinatenContext extends ParserRuleContext {
		public TerminalNode U_KOORDINATEN() { return getToken(LafParser.U_KOORDINATEN, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public U_koordinatenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_koordinaten; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_koordinaten(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_koordinaten(this);
		}
	}

	public final U_koordinatenContext u_koordinaten() throws RecognitionException {
		U_koordinatenContext _localctx = new U_koordinatenContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_u_koordinaten);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(700);
			match(U_KOORDINATEN);
			setState(704);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(701);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(702);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(703);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(706);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_koordinaten_sContext extends ParserRuleContext {
		public TerminalNode U_KOORDINATEN_S() { return getToken(LafParser.U_KOORDINATEN_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public U_koordinaten_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_koordinaten_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_koordinaten_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_koordinaten_s(this);
		}
	}

	public final U_koordinaten_sContext u_koordinaten_s() throws RecognitionException {
		U_koordinaten_sContext _localctx = new U_koordinaten_sContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_u_koordinaten_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(708);
			match(U_KOORDINATEN_S);
			setState(712);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(709);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(710);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(711);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(714);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_orts_zusatzcodeContext extends ParserRuleContext {
		public TerminalNode U_ORTS_ZUSATZCODE() { return getToken(LafParser.U_ORTS_ZUSATZCODE, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public U_orts_zusatzcodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_orts_zusatzcode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_orts_zusatzcode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_orts_zusatzcode(this);
		}
	}

	public final U_orts_zusatzcodeContext u_orts_zusatzcode() throws RecognitionException {
		U_orts_zusatzcodeContext _localctx = new U_orts_zusatzcodeContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_u_orts_zusatzcode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(716);
			match(U_ORTS_ZUSATZCODE);
			setState(718);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(717);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(720);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_orts_zusatztextContext extends ParserRuleContext {
		public TerminalNode U_ORTS_ZUSATZTEXT() { return getToken(LafParser.U_ORTS_ZUSATZTEXT, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public U_orts_zusatztextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_orts_zusatztext; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_orts_zusatztext(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_orts_zusatztext(this);
		}
	}

	public final U_orts_zusatztextContext u_orts_zusatztext() throws RecognitionException {
		U_orts_zusatztextContext _localctx = new U_orts_zusatztextContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_u_orts_zusatztext);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(722);
			match(U_ORTS_ZUSATZTEXT);
			setState(724);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(723);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(726);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class U_nuts_codeContext extends ParserRuleContext {
		public TerminalNode U_NUTS_CODE() { return getToken(LafParser.U_NUTS_CODE, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public U_nuts_codeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_u_nuts_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterU_nuts_code(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitU_nuts_code(this);
		}
	}

	public final U_nuts_codeContext u_nuts_code() throws RecognitionException {
		U_nuts_codeContext _localctx = new U_nuts_codeContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_u_nuts_code);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(728);
			match(U_NUTS_CODE);
			setState(730);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(729);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(732);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_herkunftsland_langContext extends ParserRuleContext {
		public TerminalNode P_HERKUNFTSLAND_LANG() { return getToken(LafParser.P_HERKUNFTSLAND_LANG, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_herkunftsland_langContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_herkunftsland_lang; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_herkunftsland_lang(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_herkunftsland_lang(this);
		}
	}

	public final P_herkunftsland_langContext p_herkunftsland_lang() throws RecognitionException {
		P_herkunftsland_langContext _localctx = new P_herkunftsland_langContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_p_herkunftsland_lang);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(734);
			match(P_HERKUNFTSLAND_LANG);
			setState(736);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(735);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(738);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_herkunftsland_kurzContext extends ParserRuleContext {
		public TerminalNode P_HERKUNFTSLAND_KURZ() { return getToken(LafParser.P_HERKUNFTSLAND_KURZ, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_herkunftsland_kurzContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_herkunftsland_kurz; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_herkunftsland_kurz(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_herkunftsland_kurz(this);
		}
	}

	public final P_herkunftsland_kurzContext p_herkunftsland_kurz() throws RecognitionException {
		P_herkunftsland_kurzContext _localctx = new P_herkunftsland_kurzContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_p_herkunftsland_kurz);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(740);
			match(P_HERKUNFTSLAND_KURZ);
			setState(742);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(741);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(744);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_herkunftsland_sContext extends ParserRuleContext {
		public TerminalNode P_HERKUNFTSLAND_S() { return getToken(LafParser.P_HERKUNFTSLAND_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_herkunftsland_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_herkunftsland_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_herkunftsland_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_herkunftsland_s(this);
		}
	}

	public final P_herkunftsland_sContext p_herkunftsland_s() throws RecognitionException {
		P_herkunftsland_sContext _localctx = new P_herkunftsland_sContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_p_herkunftsland_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(746);
			match(P_HERKUNFTSLAND_S);
			setState(748);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(747);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(750);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_gemeindeschluesselContext extends ParserRuleContext {
		public TerminalNode P_GEMEINDESCHLUESSEL() { return getToken(LafParser.P_GEMEINDESCHLUESSEL, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_gemeindeschluesselContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_gemeindeschluessel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_gemeindeschluessel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_gemeindeschluessel(this);
		}
	}

	public final P_gemeindeschluesselContext p_gemeindeschluessel() throws RecognitionException {
		P_gemeindeschluesselContext _localctx = new P_gemeindeschluesselContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_p_gemeindeschluessel);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(752);
			match(P_GEMEINDESCHLUESSEL);
			setState(754);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(753);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(756);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_gemeindenameContext extends ParserRuleContext {
		public TerminalNode P_GEMEINDENAME() { return getToken(LafParser.P_GEMEINDENAME, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_gemeindenameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_gemeindename; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_gemeindename(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_gemeindename(this);
		}
	}

	public final P_gemeindenameContext p_gemeindename() throws RecognitionException {
		P_gemeindenameContext _localctx = new P_gemeindenameContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_p_gemeindename);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(758);
			match(P_GEMEINDENAME);
			setState(760);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(759);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(762);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_orts_zusatzkennzahlContext extends ParserRuleContext {
		public TerminalNode P_ORTS_ZUSATZKENNZAHL() { return getToken(LafParser.P_ORTS_ZUSATZKENNZAHL, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_orts_zusatzkennzahlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_orts_zusatzkennzahl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_orts_zusatzkennzahl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_orts_zusatzkennzahl(this);
		}
	}

	public final P_orts_zusatzkennzahlContext p_orts_zusatzkennzahl() throws RecognitionException {
		P_orts_zusatzkennzahlContext _localctx = new P_orts_zusatzkennzahlContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_p_orts_zusatzkennzahl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(764);
			match(P_ORTS_ZUSATZKENNZAHL);
			setState(766);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(765);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(768);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_koordinatenContext extends ParserRuleContext {
		public TerminalNode P_KOORDINATEN() { return getToken(LafParser.P_KOORDINATEN, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public P_koordinatenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_koordinaten; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_koordinaten(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_koordinaten(this);
		}
	}

	public final P_koordinatenContext p_koordinaten() throws RecognitionException {
		P_koordinatenContext _localctx = new P_koordinatenContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_p_koordinaten);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(770);
			match(P_KOORDINATEN);
			setState(774);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(771);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(772);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(773);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(776);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_koordinaten_sContext extends ParserRuleContext {
		public TerminalNode P_KOORDINATEN_S() { return getToken(LafParser.P_KOORDINATEN_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public P_koordinaten_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_koordinaten_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_koordinaten_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_koordinaten_s(this);
		}
	}

	public final P_koordinaten_sContext p_koordinaten_s() throws RecognitionException {
		P_koordinaten_sContext _localctx = new P_koordinaten_sContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_p_koordinaten_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(778);
			match(P_KOORDINATEN_S);
			setState(782);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(779);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(780);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(781);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(784);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_orts_zusatzcodeContext extends ParserRuleContext {
		public TerminalNode P_ORTS_ZUSATZCODE() { return getToken(LafParser.P_ORTS_ZUSATZCODE, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_orts_zusatzcodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_orts_zusatzcode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_orts_zusatzcode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_orts_zusatzcode(this);
		}
	}

	public final P_orts_zusatzcodeContext p_orts_zusatzcode() throws RecognitionException {
		P_orts_zusatzcodeContext _localctx = new P_orts_zusatzcodeContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_p_orts_zusatzcode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(786);
			match(P_ORTS_ZUSATZCODE);
			setState(788);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(787);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(790);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_orts_zusatztextContext extends ParserRuleContext {
		public TerminalNode P_ORTS_ZUSATZTEXT() { return getToken(LafParser.P_ORTS_ZUSATZTEXT, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_orts_zusatztextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_orts_zusatztext; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_orts_zusatztext(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_orts_zusatztext(this);
		}
	}

	public final P_orts_zusatztextContext p_orts_zusatztext() throws RecognitionException {
		P_orts_zusatztextContext _localctx = new P_orts_zusatztextContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_p_orts_zusatztext);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(792);
			match(P_ORTS_ZUSATZTEXT);
			setState(794);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(793);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(796);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_nuts_codeContext extends ParserRuleContext {
		public TerminalNode P_NUTS_CODE() { return getToken(LafParser.P_NUTS_CODE, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_nuts_codeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_nuts_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_nuts_code(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_nuts_code(this);
		}
	}

	public final P_nuts_codeContext p_nuts_code() throws RecognitionException {
		P_nuts_codeContext _localctx = new P_nuts_codeContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_p_nuts_code);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(798);
			match(P_NUTS_CODE);
			setState(800);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(799);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(802);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_site_idContext extends ParserRuleContext {
		public TerminalNode P_SITE_ID() { return getToken(LafParser.P_SITE_ID, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_site_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_site_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_site_id(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_site_id(this);
		}
	}

	public final P_site_idContext p_site_id() throws RecognitionException {
		P_site_idContext _localctx = new P_site_idContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_p_site_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(804);
			match(P_SITE_ID);
			setState(806);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(805);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(808);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_site_nameContext extends ParserRuleContext {
		public TerminalNode P_SITE_NAME() { return getToken(LafParser.P_SITE_NAME, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_site_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_site_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_site_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_site_name(this);
		}
	}

	public final P_site_nameContext p_site_name() throws RecognitionException {
		P_site_nameContext _localctx = new P_site_nameContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_p_site_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(810);
			match(P_SITE_NAME);
			setState(812);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(811);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(814);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_hoehe_nnContext extends ParserRuleContext {
		public TerminalNode P_HOEHE_NN() { return getToken(LafParser.P_HOEHE_NN, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_hoehe_nnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_hoehe_nn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_hoehe_nn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_hoehe_nn(this);
		}
	}

	public final P_hoehe_nnContext p_hoehe_nn() throws RecognitionException {
		P_hoehe_nnContext _localctx = new P_hoehe_nnContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_p_hoehe_nn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(816);
			match(P_HOEHE_NN);
			setState(818);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(817);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(820);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class P_hoehe_landContext extends ParserRuleContext {
		public TerminalNode P_HOEHE_LAND() { return getToken(LafParser.P_HOEHE_LAND, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public P_hoehe_landContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_p_hoehe_land; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterP_hoehe_land(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitP_hoehe_land(this);
		}
	}

	public final P_hoehe_landContext p_hoehe_land() throws RecognitionException {
		P_hoehe_landContext _localctx = new P_hoehe_landContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_p_hoehe_land);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(822);
			match(P_HOEHE_LAND);
			setState(824);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(823);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(826);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MehrzweckfeldContext extends ParserRuleContext {
		public TerminalNode MEHRZWECKFELD() { return getToken(LafParser.MEHRZWECKFELD, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public MehrzweckfeldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mehrzweckfeld; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMehrzweckfeld(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMehrzweckfeld(this);
		}
	}

	public final MehrzweckfeldContext mehrzweckfeld() throws RecognitionException {
		MehrzweckfeldContext _localctx = new MehrzweckfeldContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_mehrzweckfeld);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(828);
			match(MEHRZWECKFELD);
			setState(830);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(829);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(832);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Mess_datum_uhrzeitContext extends ParserRuleContext {
		public TerminalNode MESS_DATUM_UHRZEIT() { return getToken(LafParser.MESS_DATUM_UHRZEIT, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Mess_datum_uhrzeitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mess_datum_uhrzeit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMess_datum_uhrzeit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMess_datum_uhrzeit(this);
		}
	}

	public final Mess_datum_uhrzeitContext mess_datum_uhrzeit() throws RecognitionException {
		Mess_datum_uhrzeitContext _localctx = new Mess_datum_uhrzeitContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_mess_datum_uhrzeit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(834);
			match(MESS_DATUM_UHRZEIT);
			setState(837);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(835);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(836);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(839);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messzeit_sekundenContext extends ParserRuleContext {
		public TerminalNode MESSZEIT_SEKUNDEN() { return getToken(LafParser.MESSZEIT_SEKUNDEN, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Messzeit_sekundenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messzeit_sekunden; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMesszeit_sekunden(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMesszeit_sekunden(this);
		}
	}

	public final Messzeit_sekundenContext messzeit_sekunden() throws RecognitionException {
		Messzeit_sekundenContext _localctx = new Messzeit_sekundenContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_messzeit_sekunden);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(841);
			match(MESSZEIT_SEKUNDEN);
			setState(843);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(842);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(845);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messmethode_cContext extends ParserRuleContext {
		public TerminalNode MESSMETHODE_C() { return getToken(LafParser.MESSMETHODE_C, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Messmethode_cContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messmethode_c; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMessmethode_c(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMessmethode_c(this);
		}
	}

	public final Messmethode_cContext messmethode_c() throws RecognitionException {
		Messmethode_cContext _localctx = new Messmethode_cContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_messmethode_c);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(847);
			match(MESSMETHODE_C);
			setState(849);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(848);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(851);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messmethode_sContext extends ParserRuleContext {
		public TerminalNode MESSMETHODE_S() { return getToken(LafParser.MESSMETHODE_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Messmethode_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messmethode_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMessmethode_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMessmethode_s(this);
		}
	}

	public final Messmethode_sContext messmethode_s() throws RecognitionException {
		Messmethode_sContext _localctx = new Messmethode_sContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_messmethode_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(853);
			match(MESSMETHODE_S);
			setState(855);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(854);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(857);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BearbeitungsstatusContext extends ParserRuleContext {
		public TerminalNode BEARBEITUNGSSTATUS() { return getToken(LafParser.BEARBEITUNGSSTATUS, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public BearbeitungsstatusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bearbeitungsstatus; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterBearbeitungsstatus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitBearbeitungsstatus(this);
		}
	}

	public final BearbeitungsstatusContext bearbeitungsstatus() throws RecognitionException {
		BearbeitungsstatusContext _localctx = new BearbeitungsstatusContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_bearbeitungsstatus);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(859);
			match(BEARBEITUNGSSTATUS);
			setState(861);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(860);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(863);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pep_flagContext extends ParserRuleContext {
		public TerminalNode PEP_FLAG() { return getToken(LafParser.PEP_FLAG, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Pep_flagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pep_flag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterPep_flag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitPep_flag(this);
		}
	}

	public final Pep_flagContext pep_flag() throws RecognitionException {
		Pep_flagContext _localctx = new Pep_flagContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_pep_flag);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(865);
			match(PEP_FLAG);
			setState(867);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(866);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(869);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Erfassung_abgeschlossenContext extends ParserRuleContext {
		public TerminalNode ERFASSUNG_ABGESCHLOSSEN() { return getToken(LafParser.ERFASSUNG_ABGESCHLOSSEN, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Erfassung_abgeschlossenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_erfassung_abgeschlossen; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterErfassung_abgeschlossen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitErfassung_abgeschlossen(this);
		}
	}

	public final Erfassung_abgeschlossenContext erfassung_abgeschlossen() throws RecognitionException {
		Erfassung_abgeschlossenContext _localctx = new Erfassung_abgeschlossenContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_erfassung_abgeschlossen);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(871);
			match(ERFASSUNG_ABGESCHLOSSEN);
			setState(873);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(872);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(875);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProbenzusatzbeschreibungContext extends ParserRuleContext {
		public TerminalNode PROBENZUSATZBESCHREIBUNG() { return getToken(LafParser.PROBENZUSATZBESCHREIBUNG, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public ProbenzusatzbeschreibungContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probenzusatzbeschreibung; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbenzusatzbeschreibung(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbenzusatzbeschreibung(this);
		}
	}

	public final ProbenzusatzbeschreibungContext probenzusatzbeschreibung() throws RecognitionException {
		ProbenzusatzbeschreibungContext _localctx = new ProbenzusatzbeschreibungContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_probenzusatzbeschreibung);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(877);
			match(PROBENZUSATZBESCHREIBUNG);
			setState(882);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(878);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(879);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(880);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(881);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(884);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pzb_sContext extends ParserRuleContext {
		public TerminalNode PZB_S() { return getToken(LafParser.PZB_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Pzb_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pzb_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterPzb_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitPzb_s(this);
		}
	}

	public final Pzb_sContext pzb_s() throws RecognitionException {
		Pzb_sContext _localctx = new Pzb_sContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_pzb_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(886);
			match(PZB_S);
			setState(891);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(887);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(888);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(889);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(890);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(893);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MesswertContext extends ParserRuleContext {
		public TerminalNode MESSWERT() { return getToken(LafParser.MESSWERT, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public MesswertContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messwert; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMesswert(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMesswert(this);
		}
	}

	public final MesswertContext messwert() throws RecognitionException {
		MesswertContext _localctx = new MesswertContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_messwert);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(895);
			match(MESSWERT);
			setState(902);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(896);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(897);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(898);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(900);
				_la = _input.LA(1);
				if (_la==STRING_ESC || _la==STRING) {
					{
					setState(899);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
				}

				}
			}

			setState(904);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messwert_sContext extends ParserRuleContext {
		public TerminalNode MESSWERT_S() { return getToken(LafParser.MESSWERT_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Messwert_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messwert_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMesswert_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMesswert_s(this);
		}
	}

	public final Messwert_sContext messwert_s() throws RecognitionException {
		Messwert_sContext _localctx = new Messwert_sContext(_ctx, getState());
		enterRule(_localctx, 210, RULE_messwert_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(906);
			match(MESSWERT_S);
			setState(913);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(907);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(908);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(909);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(911);
				_la = _input.LA(1);
				if (_la==STRING_ESC || _la==STRING) {
					{
					setState(910);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
				}

				}
			}

			setState(915);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messwert_iContext extends ParserRuleContext {
		public TerminalNode MESSWERT_I() { return getToken(LafParser.MESSWERT_I, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Messwert_iContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messwert_i; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMesswert_i(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMesswert_i(this);
		}
	}

	public final Messwert_iContext messwert_i() throws RecognitionException {
		Messwert_iContext _localctx = new Messwert_iContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_messwert_i);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(917);
			match(MESSWERT_I);
			setState(933);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(918);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(919);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(920);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(922);
				switch ( getInterpreter().adaptivePredict(_input,109,_ctx) ) {
				case 1:
					{
					setState(921);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(925);
				switch ( getInterpreter().adaptivePredict(_input,110,_ctx) ) {
				case 1:
					{
					setState(924);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(928);
				switch ( getInterpreter().adaptivePredict(_input,111,_ctx) ) {
				case 1:
					{
					setState(927);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(931);
				_la = _input.LA(1);
				if (_la==STRING_ESC || _la==STRING) {
					{
					setState(930);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
				}

				}
			}

			setState(935);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messwert_gContext extends ParserRuleContext {
		public TerminalNode MESSWERT_G() { return getToken(LafParser.MESSWERT_G, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Messwert_gContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messwert_g; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMesswert_g(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMesswert_g(this);
		}
	}

	public final Messwert_gContext messwert_g() throws RecognitionException {
		Messwert_gContext _localctx = new Messwert_gContext(_ctx, getState());
		enterRule(_localctx, 214, RULE_messwert_g);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(937);
			match(MESSWERT_G);
			setState(953);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(938);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(939);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(940);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(942);
				switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
				case 1:
					{
					setState(941);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(945);
				switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
				case 1:
					{
					setState(944);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(948);
				switch ( getInterpreter().adaptivePredict(_input,116,_ctx) ) {
				case 1:
					{
					setState(947);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(951);
				_la = _input.LA(1);
				if (_la==STRING_ESC || _la==STRING) {
					{
					setState(950);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
				}

				}
			}

			setState(955);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messwert_nwgContext extends ParserRuleContext {
		public TerminalNode MESSWERT_NWG() { return getToken(LafParser.MESSWERT_NWG, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Messwert_nwgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messwert_nwg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMesswert_nwg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMesswert_nwg(this);
		}
	}

	public final Messwert_nwgContext messwert_nwg() throws RecognitionException {
		Messwert_nwgContext _localctx = new Messwert_nwgContext(_ctx, getState());
		enterRule(_localctx, 216, RULE_messwert_nwg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(957);
			match(MESSWERT_NWG);
			setState(967);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(958);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(959);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(960);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(962);
				switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
				case 1:
					{
					setState(961);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(965);
				_la = _input.LA(1);
				if (_la==STRING_ESC || _la==STRING) {
					{
					setState(964);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
				}

				}
			}

			setState(969);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messwert_nwg_sContext extends ParserRuleContext {
		public TerminalNode MESSWERT_NWG_S() { return getToken(LafParser.MESSWERT_NWG_S, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Messwert_nwg_sContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messwert_nwg_s; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMesswert_nwg_s(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMesswert_nwg_s(this);
		}
	}

	public final Messwert_nwg_sContext messwert_nwg_s() throws RecognitionException {
		Messwert_nwg_sContext _localctx = new Messwert_nwg_sContext(_ctx, getState());
		enterRule(_localctx, 218, RULE_messwert_nwg_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(971);
			match(MESSWERT_NWG_S);
			setState(981);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(972);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(973);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(974);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(976);
				switch ( getInterpreter().adaptivePredict(_input,122,_ctx) ) {
				case 1:
					{
					setState(975);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(979);
				_la = _input.LA(1);
				if (_la==STRING_ESC || _la==STRING) {
					{
					setState(978);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
				}

				}
			}

			setState(983);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messwert_nwg_iContext extends ParserRuleContext {
		public TerminalNode MESSWERT_NWG_I() { return getToken(LafParser.MESSWERT_NWG_I, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Messwert_nwg_iContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messwert_nwg_i; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMesswert_nwg_i(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMesswert_nwg_i(this);
		}
	}

	public final Messwert_nwg_iContext messwert_nwg_i() throws RecognitionException {
		Messwert_nwg_iContext _localctx = new Messwert_nwg_iContext(_ctx, getState());
		enterRule(_localctx, 220, RULE_messwert_nwg_i);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(985);
			match(MESSWERT_NWG_I);
			setState(1004);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(986);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(987);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(988);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(990);
				switch ( getInterpreter().adaptivePredict(_input,125,_ctx) ) {
				case 1:
					{
					setState(989);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(993);
				switch ( getInterpreter().adaptivePredict(_input,126,_ctx) ) {
				case 1:
					{
					setState(992);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(996);
				switch ( getInterpreter().adaptivePredict(_input,127,_ctx) ) {
				case 1:
					{
					setState(995);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(999);
				switch ( getInterpreter().adaptivePredict(_input,128,_ctx) ) {
				case 1:
					{
					setState(998);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(1002);
				_la = _input.LA(1);
				if (_la==STRING_ESC || _la==STRING) {
					{
					setState(1001);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
				}

				}
			}

			setState(1006);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Messwert_nwg_gContext extends ParserRuleContext {
		public TerminalNode MESSWERT_NWG_G() { return getToken(LafParser.MESSWERT_NWG_G, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public Messwert_nwg_gContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messwert_nwg_g; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterMesswert_nwg_g(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitMesswert_nwg_g(this);
		}
	}

	public final Messwert_nwg_gContext messwert_nwg_g() throws RecognitionException {
		Messwert_nwg_gContext _localctx = new Messwert_nwg_gContext(_ctx, getState());
		enterRule(_localctx, 222, RULE_messwert_nwg_g);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1008);
			match(MESSWERT_NWG_G);
			setState(1027);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(1009);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(1010);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(1011);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(1013);
				switch ( getInterpreter().adaptivePredict(_input,131,_ctx) ) {
				case 1:
					{
					setState(1012);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(1016);
				switch ( getInterpreter().adaptivePredict(_input,132,_ctx) ) {
				case 1:
					{
					setState(1015);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(1019);
				switch ( getInterpreter().adaptivePredict(_input,133,_ctx) ) {
				case 1:
					{
					setState(1018);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(1022);
				switch ( getInterpreter().adaptivePredict(_input,134,_ctx) ) {
				case 1:
					{
					setState(1021);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
					break;
				}
				setState(1025);
				_la = _input.LA(1);
				if (_la==STRING_ESC || _la==STRING) {
					{
					setState(1024);
					_la = _input.LA(1);
					if ( !(_la==STRING_ESC || _la==STRING) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					}
				}

				}
			}

			setState(1029);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KommentarContext extends ParserRuleContext {
		public TerminalNode KOMMENTAR() { return getToken(LafParser.KOMMENTAR, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public KommentarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kommentar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterKommentar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitKommentar(this);
		}
	}

	public final KommentarContext kommentar() throws RecognitionException {
		KommentarContext _localctx = new KommentarContext(_ctx, getState());
		enterRule(_localctx, 224, RULE_kommentar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1031);
			match(KOMMENTAR);
			setState(1036);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(1032);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(1033);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(1034);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(1035);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(1038);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Kommentar_tContext extends ParserRuleContext {
		public TerminalNode KOMMENTAR_T() { return getToken(LafParser.KOMMENTAR_T, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Kommentar_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kommentar_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterKommentar_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitKommentar_t(this);
		}
	}

	public final Kommentar_tContext kommentar_t() throws RecognitionException {
		Kommentar_tContext _localctx = new Kommentar_tContext(_ctx, getState());
		enterRule(_localctx, 226, RULE_kommentar_t);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1040);
			match(KOMMENTAR_T);
			setState(1042);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(1041);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(1044);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProbenkommentarContext extends ParserRuleContext {
		public TerminalNode PROBENKOMMENTAR() { return getToken(LafParser.PROBENKOMMENTAR, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public ProbenkommentarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probenkommentar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbenkommentar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbenkommentar(this);
		}
	}

	public final ProbenkommentarContext probenkommentar() throws RecognitionException {
		ProbenkommentarContext _localctx = new ProbenkommentarContext(_ctx, getState());
		enterRule(_localctx, 228, RULE_probenkommentar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1046);
			match(PROBENKOMMENTAR);
			setState(1051);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(1047);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(1048);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(1049);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(1050);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(1053);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Probenkommentar_tContext extends ParserRuleContext {
		public TerminalNode PROBENKOMMENTAR_T() { return getToken(LafParser.PROBENKOMMENTAR_T, 0); }
		public TerminalNode NEWLINE() { return getToken(LafParser.NEWLINE, 0); }
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
		public TerminalNode STRING() { return getToken(LafParser.STRING, 0); }
		public Probenkommentar_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_probenkommentar_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterProbenkommentar_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitProbenkommentar_t(this);
		}
	}

	public final Probenkommentar_tContext probenkommentar_t() throws RecognitionException {
		Probenkommentar_tContext _localctx = new Probenkommentar_tContext(_ctx, getState());
		enterRule(_localctx, 230, RULE_probenkommentar_t);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1055);
			match(PROBENKOMMENTAR_T);
			setState(1057);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(1056);
				_la = _input.LA(1);
				if ( !(_la==STRING_ESC || _la==STRING) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(1059);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3a\u0428\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k\t"+
		"k\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\4t\tt\4u\tu\3\2\7\2"+
		"\u00ec\n\2\f\2\16\2\u00ef\13\2\3\2\5\2\u00f2\n\2\3\2\3\2\3\3\3\3\5\3\u00f8"+
		"\n\3\3\4\3\4\3\4\6\4\u00fd\n\4\r\4\16\4\u00fe\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u0122\n\5\3\6\5\6\u0125\n\6"+
		"\3\6\6\6\u0128\n\6\r\6\16\6\u0129\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\5\b\u0139\n\b\3\t\3\t\5\t\u013d\n\t\3\n\3\n\5\n\u0141"+
		"\n\n\3\13\3\13\5\13\u0145\n\13\3\f\3\f\5\f\u0149\n\f\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u0156\n\r\3\16\3\16\3\16\5\16\u015b\n\16"+
		"\3\17\3\17\5\17\u015f\n\17\3\20\3\20\5\20\u0163\n\20\3\21\3\21\5\21\u0167"+
		"\n\21\3\22\3\22\5\22\u016b\n\22\3\23\3\23\5\23\u016f\n\23\3\24\3\24\3"+
		"\24\5\24\u0174\n\24\3\25\3\25\5\25\u0178\n\25\3\26\3\26\5\26\u017c\n\26"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u0186\n\27\3\30\3\30\5\30"+
		"\u018a\n\30\3\31\3\31\5\31\u018e\n\31\3\32\3\32\6\32\u0192\n\32\r\32\16"+
		"\32\u0193\5\32\u0196\n\32\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34"+
		"\3\34\5\34\u01a2\n\34\3\35\3\35\3\35\5\35\u01a7\n\35\3\36\3\36\5\36\u01ab"+
		"\n\36\3\37\3\37\5\37\u01af\n\37\3 \3 \5 \u01b3\n \3 \3 \3!\3!\5!\u01b9"+
		"\n!\3!\3!\3\"\3\"\5\"\u01bf\n\"\3\"\3\"\3#\3#\5#\u01c5\n#\3#\3#\3$\3$"+
		"\5$\u01cb\n$\3$\3$\3%\3%\5%\u01d1\n%\3%\3%\3&\3&\5&\u01d7\n&\3&\3&\3\'"+
		"\3\'\5\'\u01dd\n\'\3\'\3\'\3(\3(\5(\u01e3\n(\3(\3(\3)\3)\5)\u01e9\n)\3"+
		")\3)\3*\3*\5*\u01ef\n*\3*\3*\3+\3+\5+\u01f5\n+\3+\3+\3,\3,\5,\u01fb\n"+
		",\3,\3,\3-\3-\5-\u0201\n-\3-\3-\3.\3.\5.\u0207\n.\3.\3.\3/\3/\5/\u020d"+
		"\n/\3/\3/\3\60\3\60\5\60\u0213\n\60\3\60\3\60\3\61\3\61\5\61\u0219\n\61"+
		"\3\61\3\61\3\62\3\62\5\62\u021f\n\62\3\62\3\62\3\63\3\63\5\63\u0225\n"+
		"\63\3\63\3\63\3\64\3\64\5\64\u022b\n\64\3\64\3\64\3\65\3\65\5\65\u0231"+
		"\n\65\3\65\3\65\3\66\3\66\5\66\u0237\n\66\3\66\3\66\3\67\3\67\3\67\5\67"+
		"\u023e\n\67\3\67\3\67\38\38\38\58\u0245\n8\38\38\39\39\39\59\u024c\n9"+
		"\39\39\3:\3:\3:\5:\u0253\n:\3:\3:\3;\3;\3;\5;\u025a\n;\3;\3;\3<\3<\5<"+
		"\u0260\n<\3<\3<\3=\3=\5=\u0266\n=\3=\3=\3>\3>\5>\u026c\n>\3>\3>\3?\3?"+
		"\5?\u0272\n?\3?\3?\3@\3@\5@\u0278\n@\3@\3@\3A\3A\3A\5A\u027f\nA\3A\3A"+
		"\3B\3B\5B\u0285\nB\3B\3B\3C\3C\5C\u028b\nC\3C\3C\3D\3D\5D\u0291\nD\3D"+
		"\3D\3E\3E\5E\u0297\nE\3E\3E\3F\3F\5F\u029d\nF\3F\3F\3G\3G\5G\u02a3\nG"+
		"\3G\3G\3H\3H\5H\u02a9\nH\3H\3H\3I\3I\5I\u02af\nI\3I\3I\3J\3J\5J\u02b5"+
		"\nJ\3J\3J\3K\3K\5K\u02bb\nK\3K\3K\3L\3L\3L\3L\5L\u02c3\nL\3L\3L\3M\3M"+
		"\3M\3M\5M\u02cb\nM\3M\3M\3N\3N\5N\u02d1\nN\3N\3N\3O\3O\5O\u02d7\nO\3O"+
		"\3O\3P\3P\5P\u02dd\nP\3P\3P\3Q\3Q\5Q\u02e3\nQ\3Q\3Q\3R\3R\5R\u02e9\nR"+
		"\3R\3R\3S\3S\5S\u02ef\nS\3S\3S\3T\3T\5T\u02f5\nT\3T\3T\3U\3U\5U\u02fb"+
		"\nU\3U\3U\3V\3V\5V\u0301\nV\3V\3V\3W\3W\3W\3W\5W\u0309\nW\3W\3W\3X\3X"+
		"\3X\3X\5X\u0311\nX\3X\3X\3Y\3Y\5Y\u0317\nY\3Y\3Y\3Z\3Z\5Z\u031d\nZ\3Z"+
		"\3Z\3[\3[\5[\u0323\n[\3[\3[\3\\\3\\\5\\\u0329\n\\\3\\\3\\\3]\3]\5]\u032f"+
		"\n]\3]\3]\3^\3^\5^\u0335\n^\3^\3^\3_\3_\5_\u033b\n_\3_\3_\3`\3`\5`\u0341"+
		"\n`\3`\3`\3a\3a\3a\5a\u0348\na\3a\3a\3b\3b\5b\u034e\nb\3b\3b\3c\3c\5c"+
		"\u0354\nc\3c\3c\3d\3d\5d\u035a\nd\3d\3d\3e\3e\5e\u0360\ne\3e\3e\3f\3f"+
		"\5f\u0366\nf\3f\3f\3g\3g\5g\u036c\ng\3g\3g\3h\3h\3h\3h\3h\5h\u0375\nh"+
		"\3h\3h\3i\3i\3i\3i\3i\5i\u037e\ni\3i\3i\3j\3j\3j\3j\3j\5j\u0387\nj\5j"+
		"\u0389\nj\3j\3j\3k\3k\3k\3k\3k\5k\u0392\nk\5k\u0394\nk\3k\3k\3l\3l\3l"+
		"\3l\3l\5l\u039d\nl\3l\5l\u03a0\nl\3l\5l\u03a3\nl\3l\5l\u03a6\nl\5l\u03a8"+
		"\nl\3l\3l\3m\3m\3m\3m\3m\5m\u03b1\nm\3m\5m\u03b4\nm\3m\5m\u03b7\nm\3m"+
		"\5m\u03ba\nm\5m\u03bc\nm\3m\3m\3n\3n\3n\3n\3n\5n\u03c5\nn\3n\5n\u03c8"+
		"\nn\5n\u03ca\nn\3n\3n\3o\3o\3o\3o\3o\5o\u03d3\no\3o\5o\u03d6\no\5o\u03d8"+
		"\no\3o\3o\3p\3p\3p\3p\3p\5p\u03e1\np\3p\5p\u03e4\np\3p\5p\u03e7\np\3p"+
		"\5p\u03ea\np\3p\5p\u03ed\np\5p\u03ef\np\3p\3p\3q\3q\3q\3q\3q\5q\u03f8"+
		"\nq\3q\5q\u03fb\nq\3q\5q\u03fe\nq\3q\5q\u0401\nq\3q\5q\u0404\nq\5q\u0406"+
		"\nq\3q\3q\3r\3r\3r\3r\3r\5r\u040f\nr\3r\3r\3s\3s\5s\u0415\ns\3s\3s\3t"+
		"\3t\3t\3t\3t\5t\u041e\nt\3t\3t\3u\3u\5u\u0424\nu\3u\3u\3u\2\2v\2\4\6\b"+
		"\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVX"+
		"Z\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c\u008e\u0090"+
		"\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2\u00a4\u00a6\u00a8"+
		"\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc\u00be\u00c0"+
		"\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0\u00d2\u00d4\u00d6\u00d8"+
		"\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6\u00e8\2\3\3\2_`\u047e\2\u00ed"+
		"\3\2\2\2\4\u00f5\3\2\2\2\6\u00f9\3\2\2\2\b\u0121\3\2\2\2\n\u0124\3\2\2"+
		"\2\f\u012b\3\2\2\2\16\u0138\3\2\2\2\20\u013c\3\2\2\2\22\u0140\3\2\2\2"+
		"\24\u0144\3\2\2\2\26\u0148\3\2\2\2\30\u0155\3\2\2\2\32\u015a\3\2\2\2\34"+
		"\u015e\3\2\2\2\36\u0162\3\2\2\2 \u0166\3\2\2\2\"\u016a\3\2\2\2$\u016e"+
		"\3\2\2\2&\u0173\3\2\2\2(\u0177\3\2\2\2*\u017b\3\2\2\2,\u0185\3\2\2\2."+
		"\u0189\3\2\2\2\60\u018d\3\2\2\2\62\u0195\3\2\2\2\64\u0197\3\2\2\2\66\u01a1"+
		"\3\2\2\28\u01a6\3\2\2\2:\u01aa\3\2\2\2<\u01ae\3\2\2\2>\u01b0\3\2\2\2@"+
		"\u01b6\3\2\2\2B\u01bc\3\2\2\2D\u01c2\3\2\2\2F\u01c8\3\2\2\2H\u01ce\3\2"+
		"\2\2J\u01d4\3\2\2\2L\u01da\3\2\2\2N\u01e0\3\2\2\2P\u01e6\3\2\2\2R\u01ec"+
		"\3\2\2\2T\u01f2\3\2\2\2V\u01f8\3\2\2\2X\u01fe\3\2\2\2Z\u0204\3\2\2\2\\"+
		"\u020a\3\2\2\2^\u0210\3\2\2\2`\u0216\3\2\2\2b\u021c\3\2\2\2d\u0222\3\2"+
		"\2\2f\u0228\3\2\2\2h\u022e\3\2\2\2j\u0234\3\2\2\2l\u023a\3\2\2\2n\u0241"+
		"\3\2\2\2p\u0248\3\2\2\2r\u024f\3\2\2\2t\u0256\3\2\2\2v\u025d\3\2\2\2x"+
		"\u0263\3\2\2\2z\u0269\3\2\2\2|\u026f\3\2\2\2~\u0275\3\2\2\2\u0080\u027b"+
		"\3\2\2\2\u0082\u0282\3\2\2\2\u0084\u0288\3\2\2\2\u0086\u028e\3\2\2\2\u0088"+
		"\u0294\3\2\2\2\u008a\u029a\3\2\2\2\u008c\u02a0\3\2\2\2\u008e\u02a6\3\2"+
		"\2\2\u0090\u02ac\3\2\2\2\u0092\u02b2\3\2\2\2\u0094\u02b8\3\2\2\2\u0096"+
		"\u02be\3\2\2\2\u0098\u02c6\3\2\2\2\u009a\u02ce\3\2\2\2\u009c\u02d4\3\2"+
		"\2\2\u009e\u02da\3\2\2\2\u00a0\u02e0\3\2\2\2\u00a2\u02e6\3\2\2\2\u00a4"+
		"\u02ec\3\2\2\2\u00a6\u02f2\3\2\2\2\u00a8\u02f8\3\2\2\2\u00aa\u02fe\3\2"+
		"\2\2\u00ac\u0304\3\2\2\2\u00ae\u030c\3\2\2\2\u00b0\u0314\3\2\2\2\u00b2"+
		"\u031a\3\2\2\2\u00b4\u0320\3\2\2\2\u00b6\u0326\3\2\2\2\u00b8\u032c\3\2"+
		"\2\2\u00ba\u0332\3\2\2\2\u00bc\u0338\3\2\2\2\u00be\u033e\3\2\2\2\u00c0"+
		"\u0344\3\2\2\2\u00c2\u034b\3\2\2\2\u00c4\u0351\3\2\2\2\u00c6\u0357\3\2"+
		"\2\2\u00c8\u035d\3\2\2\2\u00ca\u0363\3\2\2\2\u00cc\u0369\3\2\2\2\u00ce"+
		"\u036f\3\2\2\2\u00d0\u0378\3\2\2\2\u00d2\u0381\3\2\2\2\u00d4\u038c\3\2"+
		"\2\2\u00d6\u0397\3\2\2\2\u00d8\u03ab\3\2\2\2\u00da\u03bf\3\2\2\2\u00dc"+
		"\u03cd\3\2\2\2\u00de\u03db\3\2\2\2\u00e0\u03f2\3\2\2\2\u00e2\u0409\3\2"+
		"\2\2\u00e4\u0412\3\2\2\2\u00e6\u0418\3\2\2\2\u00e8\u0421\3\2\2\2\u00ea"+
		"\u00ec\5\6\4\2\u00eb\u00ea\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb\3\2"+
		"\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00f1\3\2\2\2\u00ef\u00ed\3\2\2\2\u00f0"+
		"\u00f2\5\4\3\2\u00f1\u00f0\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2\u00f3\3\2"+
		"\2\2\u00f3\u00f4\7\2\2\3\u00f4\3\3\2\2\2\u00f5\u00f7\7\3\2\2\u00f6\u00f8"+
		"\7^\2\2\u00f7\u00f6\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\5\3\2\2\2\u00f9"+
		"\u00fa\7\4\2\2\u00fa\u00fc\7^\2\2\u00fb\u00fd\5\b\5\2\u00fc\u00fb\3\2"+
		"\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00fc\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff"+
		"\7\3\2\2\2\u0100\u0122\5\20\t\2\u0101\u0122\5> \2\u0102\u0122\5@!\2\u0103"+
		"\u0122\5F$\2\u0104\u0122\5H%\2\u0105\u0122\5&\24\2\u0106\u0122\5P)\2\u0107"+
		"\u0122\5R*\2\u0108\u0122\5T+\2\u0109\u0122\5(\25\2\u010a\u0122\5\22\n"+
		"\2\u010b\u0122\5b\62\2\u010c\u0122\5d\63\2\u010d\u0122\5f\64\2\u010e\u0122"+
		"\5 \21\2\u010f\u0122\5r:\2\u0110\u0122\5t;\2\u0111\u0122\5l\67\2\u0112"+
		"\u0122\5n8\2\u0113\u0122\5\24\13\2\u0114\u0122\5z>\2\u0115\u0122\5\26"+
		"\f\2\u0116\u0122\5\u0080A\2\u0117\u0122\5\u0082B\2\u0118\u0122\5\u0084"+
		"C\2\u0119\u0122\5\u0086D\2\u011a\u0122\5\u0088E\2\u011b\u0122\5\62\32"+
		"\2\u011c\u0122\5\30\r\2\u011d\u0122\5\u00be`\2\u011e\u0122\5\n\6\2\u011f"+
		"\u0122\5\"\22\2\u0120\u0122\5$\23\2\u0121\u0100\3\2\2\2\u0121\u0101\3"+
		"\2\2\2\u0121\u0102\3\2\2\2\u0121\u0103\3\2\2\2\u0121\u0104\3\2\2\2\u0121"+
		"\u0105\3\2\2\2\u0121\u0106\3\2\2\2\u0121\u0107\3\2\2\2\u0121\u0108\3\2"+
		"\2\2\u0121\u0109\3\2\2\2\u0121\u010a\3\2\2\2\u0121\u010b\3\2\2\2\u0121"+
		"\u010c\3\2\2\2\u0121\u010d\3\2\2\2\u0121\u010e\3\2\2\2\u0121\u010f\3\2"+
		"\2\2\u0121\u0110\3\2\2\2\u0121\u0111\3\2\2\2\u0121\u0112\3\2\2\2\u0121"+
		"\u0113\3\2\2\2\u0121\u0114\3\2\2\2\u0121\u0115\3\2\2\2\u0121\u0116\3\2"+
		"\2\2\u0121\u0117\3\2\2\2\u0121\u0118\3\2\2\2\u0121\u0119\3\2\2\2\u0121"+
		"\u011a\3\2\2\2\u0121\u011b\3\2\2\2\u0121\u011c\3\2\2\2\u0121\u011d\3\2"+
		"\2\2\u0121\u011e\3\2\2\2\u0121\u011f\3\2\2\2\u0121\u0120\3\2\2\2\u0122"+
		"\t\3\2\2\2\u0123\u0125\5\f\7\2\u0124\u0123\3\2\2\2\u0124\u0125\3\2\2\2"+
		"\u0125\u0127\3\2\2\2\u0126\u0128\5\16\b\2\u0127\u0126\3\2\2\2\u0128\u0129"+
		"\3\2\2\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a\13\3\2\2\2\u012b"+
		"\u012c\7\5\2\2\u012c\u012d\7^\2\2\u012d\r\3\2\2\2\u012e\u0139\5V,\2\u012f"+
		"\u0139\5\60\31\2\u0130\u0139\5\u00c0a\2\u0131\u0139\5\u00c2b\2\u0132\u0139"+
		"\5*\26\2\u0133\u0139\5\u00c8e\2\u0134\u0139\5\u00caf\2\u0135\u0139\5\u00cc"+
		"g\2\u0136\u0139\5,\27\2\u0137\u0139\5.\30\2\u0138\u012e\3\2\2\2\u0138"+
		"\u012f\3\2\2\2\u0138\u0130\3\2\2\2\u0138\u0131\3\2\2\2\u0138\u0132\3\2"+
		"\2\2\u0138\u0133\3\2\2\2\u0138\u0134\3\2\2\2\u0138\u0135\3\2\2\2\u0138"+
		"\u0136\3\2\2\2\u0138\u0137\3\2\2\2\u0139\17\3\2\2\2\u013a\u013d\5B\"\2"+
		"\u013b\u013d\5D#\2\u013c\u013a\3\2\2\2\u013c\u013b\3\2\2\2\u013d\21\3"+
		"\2\2\2\u013e\u0141\5^\60\2\u013f\u0141\5`\61\2\u0140\u013e\3\2\2\2\u0140"+
		"\u013f\3\2\2\2\u0141\23\3\2\2\2\u0142\u0145\5v<\2\u0143\u0145\5x=\2\u0144"+
		"\u0142\3\2\2\2\u0144\u0143\3\2\2\2\u0145\25\3\2\2\2\u0146\u0149\5|?\2"+
		"\u0147\u0149\5~@\2\u0148\u0146\3\2\2\2\u0148\u0147\3\2\2\2\u0149\27\3"+
		"\2\2\2\u014a\u0156\5\32\16\2\u014b\u0156\5\34\17\2\u014c\u0156\5\u00aa"+
		"V\2\u014d\u0156\5\36\20\2\u014e\u0156\5\u00b0Y\2\u014f\u0156\5\u00b2Z"+
		"\2\u0150\u0156\5\u00b4[\2\u0151\u0156\5\u00b6\\\2\u0152\u0156\5\u00b8"+
		"]\2\u0153\u0156\5\u00ba^\2\u0154\u0156\5\u00bc_\2\u0155\u014a\3\2\2\2"+
		"\u0155\u014b\3\2\2\2\u0155\u014c\3\2\2\2\u0155\u014d\3\2\2\2\u0155\u014e"+
		"\3\2\2\2\u0155\u014f\3\2\2\2\u0155\u0150\3\2\2\2\u0155\u0151\3\2\2\2\u0155"+
		"\u0152\3\2\2\2\u0155\u0153\3\2\2\2\u0155\u0154\3\2\2\2\u0156\31\3\2\2"+
		"\2\u0157\u015b\5\u00a0Q\2\u0158\u015b\5\u00a2R\2\u0159\u015b\5\u00a4S"+
		"\2\u015a\u0157\3\2\2\2\u015a\u0158\3\2\2\2\u015a\u0159\3\2\2\2\u015b\33"+
		"\3\2\2\2\u015c\u015f\5\u00a6T\2\u015d\u015f\5\u00a8U\2\u015e\u015c\3\2"+
		"\2\2\u015e\u015d\3\2\2\2\u015f\35\3\2\2\2\u0160\u0163\5\u00acW\2\u0161"+
		"\u0163\5\u00aeX\2\u0162\u0160\3\2\2\2\u0162\u0161\3\2\2\2\u0163\37\3\2"+
		"\2\2\u0164\u0167\5h\65\2\u0165\u0167\5j\66\2\u0166\u0164\3\2\2\2\u0166"+
		"\u0165\3\2\2\2\u0167!\3\2\2\2\u0168\u016b\5\u00ceh\2\u0169\u016b\5\u00d0"+
		"i\2\u016a\u0168\3\2\2\2\u016a\u0169\3\2\2\2\u016b#\3\2\2\2\u016c\u016f"+
		"\5\u00e6t\2\u016d\u016f\5\u00e8u\2\u016e\u016c\3\2\2\2\u016e\u016d\3\2"+
		"\2\2\u016f%\3\2\2\2\u0170\u0174\5J&\2\u0171\u0174\5L\'\2\u0172\u0174\5"+
		"N(\2\u0173\u0170\3\2\2\2\u0173\u0171\3\2\2\2\u0173\u0172\3\2\2\2\u0174"+
		"\'\3\2\2\2\u0175\u0178\5X-\2\u0176\u0178\5Z.\2\u0177\u0175\3\2\2\2\u0177"+
		"\u0176\3\2\2\2\u0178)\3\2\2\2\u0179\u017c\5\u00c4c\2\u017a\u017c\5\u00c6"+
		"d\2\u017b\u0179\3\2\2\2\u017b\u017a\3\2\2\2\u017c+\3\2\2\2\u017d\u0186"+
		"\5\u00d2j\2\u017e\u0186\5\u00d4k\2\u017f\u0186\5\u00d6l\2\u0180\u0186"+
		"\5\u00d8m\2\u0181\u0186\5\u00dan\2\u0182\u0186\5\u00dco\2\u0183\u0186"+
		"\5\u00dep\2\u0184\u0186\5\u00e0q\2\u0185\u017d\3\2\2\2\u0185\u017e\3\2"+
		"\2\2\u0185\u017f\3\2\2\2\u0185\u0180\3\2\2\2\u0185\u0181\3\2\2\2\u0185"+
		"\u0182\3\2\2\2\u0185\u0183\3\2\2\2\u0185\u0184\3\2\2\2\u0186-\3\2\2\2"+
		"\u0187\u018a\5\u00e2r\2\u0188\u018a\5\u00e4s\2\u0189\u0187\3\2\2\2\u0189"+
		"\u0188\3\2\2\2\u018a/\3\2\2\2\u018b\u018e\5X-\2\u018c\u018e\5\\/\2\u018d"+
		"\u018b\3\2\2\2\u018d\u018c\3\2\2\2\u018e\61\3\2\2\2\u018f\u0196\5\64\33"+
		"\2\u0190\u0192\5\66\34\2\u0191\u0190\3\2\2\2\u0192\u0193\3\2\2\2\u0193"+
		"\u0191\3\2\2\2\u0193\u0194\3\2\2\2\u0194\u0196\3\2\2\2\u0195\u018f\3\2"+
		"\2\2\u0195\u0191\3\2\2\2\u0196\63\3\2\2\2\u0197\u0198\7\6\2\2\u0198\u0199"+
		"\7^\2\2\u0199\65\3\2\2\2\u019a\u01a2\58\35\2\u019b\u01a2\5:\36\2\u019c"+
		"\u01a2\5\u0094K\2\u019d\u01a2\5<\37\2\u019e\u01a2\5\u009aN\2\u019f\u01a2"+
		"\5\u009cO\2\u01a0\u01a2\5\u009eP\2\u01a1\u019a\3\2\2\2\u01a1\u019b\3\2"+
		"\2\2\u01a1\u019c\3\2\2\2\u01a1\u019d\3\2\2\2\u01a1\u019e\3\2\2\2\u01a1"+
		"\u019f\3\2\2\2\u01a1\u01a0\3\2\2\2\u01a2\67\3\2\2\2\u01a3\u01a7\5\u008a"+
		"F\2\u01a4\u01a7\5\u008cG\2\u01a5\u01a7\5\u008eH\2\u01a6\u01a3\3\2\2\2"+
		"\u01a6\u01a4\3\2\2\2\u01a6\u01a5\3\2\2\2\u01a79\3\2\2\2\u01a8\u01ab\5"+
		"\u0090I\2\u01a9\u01ab\5\u0092J\2\u01aa\u01a8\3\2\2\2\u01aa\u01a9\3\2\2"+
		"\2\u01ab;\3\2\2\2\u01ac\u01af\5\u0096L\2\u01ad\u01af\5\u0098M\2\u01ae"+
		"\u01ac\3\2\2\2\u01ae\u01ad\3\2\2\2\u01af=\3\2\2\2\u01b0\u01b2\7\7\2\2"+
		"\u01b1\u01b3\t\2\2\2\u01b2\u01b1\3\2\2\2\u01b2\u01b3\3\2\2\2\u01b3\u01b4"+
		"\3\2\2\2\u01b4\u01b5\7^\2\2\u01b5?\3\2\2\2\u01b6\u01b8\7\b\2\2\u01b7\u01b9"+
		"\t\2\2\2\u01b8\u01b7\3\2\2\2\u01b8\u01b9\3\2\2\2\u01b9\u01ba\3\2\2\2\u01ba"+
		"\u01bb\7^\2\2\u01bbA\3\2\2\2\u01bc\u01be\7\t\2\2\u01bd\u01bf\t\2\2\2\u01be"+
		"\u01bd\3\2\2\2\u01be\u01bf\3\2\2\2\u01bf\u01c0\3\2\2\2\u01c0\u01c1\7^"+
		"\2\2\u01c1C\3\2\2\2\u01c2\u01c4\7\n\2\2\u01c3\u01c5\t\2\2\2\u01c4\u01c3"+
		"\3\2\2\2\u01c4\u01c5\3\2\2\2\u01c5\u01c6\3\2\2\2\u01c6\u01c7\7^\2\2\u01c7"+
		"E\3\2\2\2\u01c8\u01ca\7\13\2\2\u01c9\u01cb\t\2\2\2\u01ca\u01c9\3\2\2\2"+
		"\u01ca\u01cb\3\2\2\2\u01cb\u01cc\3\2\2\2\u01cc\u01cd\7^\2\2\u01cdG\3\2"+
		"\2\2\u01ce\u01d0\7\f\2\2\u01cf\u01d1\t\2\2\2\u01d0\u01cf\3\2\2\2\u01d0"+
		"\u01d1\3\2\2\2\u01d1\u01d2\3\2\2\2\u01d2\u01d3\7^\2\2\u01d3I\3\2\2\2\u01d4"+
		"\u01d6\7\r\2\2\u01d5\u01d7\t\2\2\2\u01d6\u01d5\3\2\2\2\u01d6\u01d7\3\2"+
		"\2\2\u01d7\u01d8\3\2\2\2\u01d8\u01d9\7^\2\2\u01d9K\3\2\2\2\u01da\u01dc"+
		"\7\16\2\2\u01db\u01dd\t\2\2\2\u01dc\u01db\3\2\2\2\u01dc\u01dd\3\2\2\2"+
		"\u01dd\u01de\3\2\2\2\u01de\u01df\7^\2\2\u01dfM\3\2\2\2\u01e0\u01e2\7\17"+
		"\2\2\u01e1\u01e3\t\2\2\2\u01e2\u01e1\3\2\2\2\u01e2\u01e3\3\2\2\2\u01e3"+
		"\u01e4\3\2\2\2\u01e4\u01e5\7^\2\2\u01e5O\3\2\2\2\u01e6\u01e8\7\20\2\2"+
		"\u01e7\u01e9\t\2\2\2\u01e8\u01e7\3\2\2\2\u01e8\u01e9\3\2\2\2\u01e9\u01ea"+
		"\3\2\2\2\u01ea\u01eb\7^\2\2\u01ebQ\3\2\2\2\u01ec\u01ee\7\21\2\2\u01ed"+
		"\u01ef\t\2\2\2\u01ee\u01ed\3\2\2\2\u01ee\u01ef\3\2\2\2\u01ef\u01f0\3\2"+
		"\2\2\u01f0\u01f1\7^\2\2\u01f1S\3\2\2\2\u01f2\u01f4\7\22\2\2\u01f3\u01f5"+
		"\t\2\2\2\u01f4\u01f3\3\2\2\2\u01f4\u01f5\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6"+
		"\u01f7\7^\2\2\u01f7U\3\2\2\2\u01f8\u01fa\7\23\2\2\u01f9\u01fb\t\2\2\2"+
		"\u01fa\u01f9\3\2\2\2\u01fa\u01fb\3\2\2\2\u01fb\u01fc\3\2\2\2\u01fc\u01fd"+
		"\7^\2\2\u01fdW\3\2\2\2\u01fe\u0200\7\24\2\2\u01ff\u0201\t\2\2\2\u0200"+
		"\u01ff\3\2\2\2\u0200\u0201\3\2\2\2\u0201\u0202\3\2\2\2\u0202\u0203\7^"+
		"\2\2\u0203Y\3\2\2\2\u0204\u0206\7\25\2\2\u0205\u0207\t\2\2\2\u0206\u0205"+
		"\3\2\2\2\u0206\u0207\3\2\2\2\u0207\u0208\3\2\2\2\u0208\u0209\7^\2\2\u0209"+
		"[\3\2\2\2\u020a\u020c\7\26\2\2\u020b\u020d\t\2\2\2\u020c\u020b\3\2\2\2"+
		"\u020c\u020d\3\2\2\2\u020d\u020e\3\2\2\2\u020e\u020f\7^\2\2\u020f]\3\2"+
		"\2\2\u0210\u0212\7\27\2\2\u0211\u0213\t\2\2\2\u0212\u0211\3\2\2\2\u0212"+
		"\u0213\3\2\2\2\u0213\u0214\3\2\2\2\u0214\u0215\7^\2\2\u0215_\3\2\2\2\u0216"+
		"\u0218\7\30\2\2\u0217\u0219\t\2\2\2\u0218\u0217\3\2\2\2\u0218\u0219\3"+
		"\2\2\2\u0219\u021a\3\2\2\2\u021a\u021b\7^\2\2\u021ba\3\2\2\2\u021c\u021e"+
		"\7\31\2\2\u021d\u021f\t\2\2\2\u021e\u021d\3\2\2\2\u021e\u021f\3\2\2\2"+
		"\u021f\u0220\3\2\2\2\u0220\u0221\7^\2\2\u0221c\3\2\2\2\u0222\u0224\7\32"+
		"\2\2\u0223\u0225\t\2\2\2\u0224\u0223\3\2\2\2\u0224\u0225\3\2\2\2\u0225"+
		"\u0226\3\2\2\2\u0226\u0227\7^\2\2\u0227e\3\2\2\2\u0228\u022a\7\33\2\2"+
		"\u0229\u022b\t\2\2\2\u022a\u0229\3\2\2\2\u022a\u022b\3\2\2\2\u022b\u022c"+
		"\3\2\2\2\u022c\u022d\7^\2\2\u022dg\3\2\2\2\u022e\u0230\7\34\2\2\u022f"+
		"\u0231\t\2\2\2\u0230\u022f\3\2\2\2\u0230\u0231\3\2\2\2\u0231\u0232\3\2"+
		"\2\2\u0232\u0233\7^\2\2\u0233i\3\2\2\2\u0234\u0236\7\35\2\2\u0235\u0237"+
		"\t\2\2\2\u0236\u0235\3\2\2\2\u0236\u0237\3\2\2\2\u0237\u0238\3\2\2\2\u0238"+
		"\u0239\7^\2\2\u0239k\3\2\2\2\u023a\u023d\7\36\2\2\u023b\u023c\t\2\2\2"+
		"\u023c\u023e\t\2\2\2\u023d\u023b\3\2\2\2\u023d\u023e\3\2\2\2\u023e\u023f"+
		"\3\2\2\2\u023f\u0240\7^\2\2\u0240m\3\2\2\2\u0241\u0244\7\37\2\2\u0242"+
		"\u0243\t\2\2\2\u0243\u0245\t\2\2\2\u0244\u0242\3\2\2\2\u0244\u0245\3\2"+
		"\2\2\u0245\u0246\3\2\2\2\u0246\u0247\7^\2\2\u0247o\3\2\2\2\u0248\u024b"+
		"\7 \2\2\u0249\u024a\t\2\2\2\u024a\u024c\t\2\2\2\u024b\u0249\3\2\2\2\u024b"+
		"\u024c\3\2\2\2\u024c\u024d\3\2\2\2\u024d\u024e\7^\2\2\u024eq\3\2\2\2\u024f"+
		"\u0252\7!\2\2\u0250\u0251\t\2\2\2\u0251\u0253\t\2\2\2\u0252\u0250\3\2"+
		"\2\2\u0252\u0253\3\2\2\2\u0253\u0254\3\2\2\2\u0254\u0255\7^\2\2\u0255"+
		"s\3\2\2\2\u0256\u0259\7\"\2\2\u0257\u0258\t\2\2\2\u0258\u025a\t\2\2\2"+
		"\u0259\u0257\3\2\2\2\u0259\u025a\3\2\2\2\u025a\u025b\3\2\2\2\u025b\u025c"+
		"\7^\2\2\u025cu\3\2\2\2\u025d\u025f\7#\2\2\u025e\u0260\t\2\2\2\u025f\u025e"+
		"\3\2\2\2\u025f\u0260\3\2\2\2\u0260\u0261\3\2\2\2\u0261\u0262\7^\2\2\u0262"+
		"w\3\2\2\2\u0263\u0265\7$\2\2\u0264\u0266\t\2\2\2\u0265\u0264\3\2\2\2\u0265"+
		"\u0266\3\2\2\2\u0266\u0267\3\2\2\2\u0267\u0268\7^\2\2\u0268y\3\2\2\2\u0269"+
		"\u026b\7%\2\2\u026a\u026c\t\2\2\2\u026b\u026a\3\2\2\2\u026b\u026c\3\2"+
		"\2\2\u026c\u026d\3\2\2\2\u026d\u026e\7^\2\2\u026e{\3\2\2\2\u026f\u0271"+
		"\7&\2\2\u0270\u0272\t\2\2\2\u0271\u0270\3\2\2\2\u0271\u0272\3\2\2\2\u0272"+
		"\u0273\3\2\2\2\u0273\u0274\7^\2\2\u0274}\3\2\2\2\u0275\u0277\7\'\2\2\u0276"+
		"\u0278\t\2\2\2\u0277\u0276\3\2\2\2\u0277\u0278\3\2\2\2\u0278\u0279\3\2"+
		"\2\2\u0279\u027a\7^\2\2\u027a\177\3\2\2\2\u027b\u027e\7(\2\2\u027c\u027d"+
		"\7`\2\2\u027d\u027f\7`\2\2\u027e\u027c\3\2\2\2\u027e\u027f\3\2\2\2\u027f"+
		"\u0280\3\2\2\2\u0280\u0281\7^\2\2\u0281\u0081\3\2\2\2\u0282\u0284\7)\2"+
		"\2\u0283\u0285\t\2\2\2\u0284\u0283\3\2\2\2\u0284\u0285\3\2\2\2\u0285\u0286"+
		"\3\2\2\2\u0286\u0287\7^\2\2\u0287\u0083\3\2\2\2\u0288\u028a\7*\2\2\u0289"+
		"\u028b\t\2\2\2\u028a\u0289\3\2\2\2\u028a\u028b\3\2\2\2\u028b\u028c\3\2"+
		"\2\2\u028c\u028d\7^\2\2\u028d\u0085\3\2\2\2\u028e\u0290\7+\2\2\u028f\u0291"+
		"\t\2\2\2\u0290\u028f\3\2\2\2\u0290\u0291\3\2\2\2\u0291\u0292\3\2\2\2\u0292"+
		"\u0293\7^\2\2\u0293\u0087\3\2\2\2\u0294\u0296\7,\2\2\u0295\u0297\t\2\2"+
		"\2\u0296\u0295\3\2\2\2\u0296\u0297\3\2\2\2\u0297\u0298\3\2\2\2\u0298\u0299"+
		"\7^\2\2\u0299\u0089\3\2\2\2\u029a\u029c\7-\2\2\u029b\u029d\t\2\2\2\u029c"+
		"\u029b\3\2\2\2\u029c\u029d\3\2\2\2\u029d\u029e\3\2\2\2\u029e\u029f\7^"+
		"\2\2\u029f\u008b\3\2\2\2\u02a0\u02a2\7.\2\2\u02a1\u02a3\t\2\2\2\u02a2"+
		"\u02a1\3\2\2\2\u02a2\u02a3\3\2\2\2\u02a3\u02a4\3\2\2\2\u02a4\u02a5\7^"+
		"\2\2\u02a5\u008d\3\2\2\2\u02a6\u02a8\7/\2\2\u02a7\u02a9\t\2\2\2\u02a8"+
		"\u02a7\3\2\2\2\u02a8\u02a9\3\2\2\2\u02a9\u02aa\3\2\2\2\u02aa\u02ab\7^"+
		"\2\2\u02ab\u008f\3\2\2\2\u02ac\u02ae\7\60\2\2\u02ad\u02af\t\2\2\2\u02ae"+
		"\u02ad\3\2\2\2\u02ae\u02af\3\2\2\2\u02af\u02b0\3\2\2\2\u02b0\u02b1\7^"+
		"\2\2\u02b1\u0091\3\2\2\2\u02b2\u02b4\7\61\2\2\u02b3\u02b5\t\2\2\2\u02b4"+
		"\u02b3\3\2\2\2\u02b4\u02b5\3\2\2\2\u02b5\u02b6\3\2\2\2\u02b6\u02b7\7^"+
		"\2\2\u02b7\u0093\3\2\2\2\u02b8\u02ba\7\62\2\2\u02b9\u02bb\t\2\2\2\u02ba"+
		"\u02b9\3\2\2\2\u02ba\u02bb\3\2\2\2\u02bb\u02bc\3\2\2\2\u02bc\u02bd\7^"+
		"\2\2\u02bd\u0095\3\2\2\2\u02be\u02c2\7\63\2\2\u02bf\u02c0\t\2\2\2\u02c0"+
		"\u02c1\t\2\2\2\u02c1\u02c3\t\2\2\2\u02c2\u02bf\3\2\2\2\u02c2\u02c3\3\2"+
		"\2\2\u02c3\u02c4\3\2\2\2\u02c4\u02c5\7^\2\2\u02c5\u0097\3\2\2\2\u02c6"+
		"\u02ca\7\64\2\2\u02c7\u02c8\t\2\2\2\u02c8\u02c9\t\2\2\2\u02c9\u02cb\t"+
		"\2\2\2\u02ca\u02c7\3\2\2\2\u02ca\u02cb\3\2\2\2\u02cb\u02cc\3\2\2\2\u02cc"+
		"\u02cd\7^\2\2\u02cd\u0099\3\2\2\2\u02ce\u02d0\7\65\2\2\u02cf\u02d1\t\2"+
		"\2\2\u02d0\u02cf\3\2\2\2\u02d0\u02d1\3\2\2\2\u02d1\u02d2\3\2\2\2\u02d2"+
		"\u02d3\7^\2\2\u02d3\u009b\3\2\2\2\u02d4\u02d6\7\66\2\2\u02d5\u02d7\t\2"+
		"\2\2\u02d6\u02d5\3\2\2\2\u02d6\u02d7\3\2\2\2\u02d7\u02d8\3\2\2\2\u02d8"+
		"\u02d9\7^\2\2\u02d9\u009d\3\2\2\2\u02da\u02dc\7\67\2\2\u02db\u02dd\t\2"+
		"\2\2\u02dc\u02db\3\2\2\2\u02dc\u02dd\3\2\2\2\u02dd\u02de\3\2\2\2\u02de"+
		"\u02df\7^\2\2\u02df\u009f\3\2\2\2\u02e0\u02e2\78\2\2\u02e1\u02e3\t\2\2"+
		"\2\u02e2\u02e1\3\2\2\2\u02e2\u02e3\3\2\2\2\u02e3\u02e4\3\2\2\2\u02e4\u02e5"+
		"\7^\2\2\u02e5\u00a1\3\2\2\2\u02e6\u02e8\79\2\2\u02e7\u02e9\t\2\2\2\u02e8"+
		"\u02e7\3\2\2\2\u02e8\u02e9\3\2\2\2\u02e9\u02ea\3\2\2\2\u02ea\u02eb\7^"+
		"\2\2\u02eb\u00a3\3\2\2\2\u02ec\u02ee\7:\2\2\u02ed\u02ef\t\2\2\2\u02ee"+
		"\u02ed\3\2\2\2\u02ee\u02ef\3\2\2\2\u02ef\u02f0\3\2\2\2\u02f0\u02f1\7^"+
		"\2\2\u02f1\u00a5\3\2\2\2\u02f2\u02f4\7;\2\2\u02f3\u02f5\t\2\2\2\u02f4"+
		"\u02f3\3\2\2\2\u02f4\u02f5\3\2\2\2\u02f5\u02f6\3\2\2\2\u02f6\u02f7\7^"+
		"\2\2\u02f7\u00a7\3\2\2\2\u02f8\u02fa\7<\2\2\u02f9\u02fb\t\2\2\2\u02fa"+
		"\u02f9\3\2\2\2\u02fa\u02fb\3\2\2\2\u02fb\u02fc\3\2\2\2\u02fc\u02fd\7^"+
		"\2\2\u02fd\u00a9\3\2\2\2\u02fe\u0300\7=\2\2\u02ff\u0301\t\2\2\2\u0300"+
		"\u02ff\3\2\2\2\u0300\u0301\3\2\2\2\u0301\u0302\3\2\2\2\u0302\u0303\7^"+
		"\2\2\u0303\u00ab\3\2\2\2\u0304\u0308\7>\2\2\u0305\u0306\t\2\2\2\u0306"+
		"\u0307\t\2\2\2\u0307\u0309\t\2\2\2\u0308\u0305\3\2\2\2\u0308\u0309\3\2"+
		"\2\2\u0309\u030a\3\2\2\2\u030a\u030b\7^\2\2\u030b\u00ad\3\2\2\2\u030c"+
		"\u0310\7?\2\2\u030d\u030e\t\2\2\2\u030e\u030f\t\2\2\2\u030f\u0311\t\2"+
		"\2\2\u0310\u030d\3\2\2\2\u0310\u0311\3\2\2\2\u0311\u0312\3\2\2\2\u0312"+
		"\u0313\7^\2\2\u0313\u00af\3\2\2\2\u0314\u0316\7@\2\2\u0315\u0317\t\2\2"+
		"\2\u0316\u0315\3\2\2\2\u0316\u0317\3\2\2\2\u0317\u0318\3\2\2\2\u0318\u0319"+
		"\7^\2\2\u0319\u00b1\3\2\2\2\u031a\u031c\7A\2\2\u031b\u031d\t\2\2\2\u031c"+
		"\u031b\3\2\2\2\u031c\u031d\3\2\2\2\u031d\u031e\3\2\2\2\u031e\u031f\7^"+
		"\2\2\u031f\u00b3\3\2\2\2\u0320\u0322\7B\2\2\u0321\u0323\t\2\2\2\u0322"+
		"\u0321\3\2\2\2\u0322\u0323\3\2\2\2\u0323\u0324\3\2\2\2\u0324\u0325\7^"+
		"\2\2\u0325\u00b5\3\2\2\2\u0326\u0328\7C\2\2\u0327\u0329\t\2\2\2\u0328"+
		"\u0327\3\2\2\2\u0328\u0329\3\2\2\2\u0329\u032a\3\2\2\2\u032a\u032b\7^"+
		"\2\2\u032b\u00b7\3\2\2\2\u032c\u032e\7D\2\2\u032d\u032f\t\2\2\2\u032e"+
		"\u032d\3\2\2\2\u032e\u032f\3\2\2\2\u032f\u0330\3\2\2\2\u0330\u0331\7^"+
		"\2\2\u0331\u00b9\3\2\2\2\u0332\u0334\7E\2\2\u0333\u0335\t\2\2\2\u0334"+
		"\u0333\3\2\2\2\u0334\u0335\3\2\2\2\u0335\u0336\3\2\2\2\u0336\u0337\7^"+
		"\2\2\u0337\u00bb\3\2\2\2\u0338\u033a\7F\2\2\u0339\u033b\t\2\2\2\u033a"+
		"\u0339\3\2\2\2\u033a\u033b\3\2\2\2\u033b\u033c\3\2\2\2\u033c\u033d\7^"+
		"\2\2\u033d\u00bd\3\2\2\2\u033e\u0340\7G\2\2\u033f\u0341\t\2\2\2\u0340"+
		"\u033f\3\2\2\2\u0340\u0341\3\2\2\2\u0341\u0342\3\2\2\2\u0342\u0343\7^"+
		"\2\2\u0343\u00bf\3\2\2\2\u0344\u0347\7H\2\2\u0345\u0346\t\2\2\2\u0346"+
		"\u0348\t\2\2\2\u0347\u0345\3\2\2\2\u0347\u0348\3\2\2\2\u0348\u0349\3\2"+
		"\2\2\u0349\u034a\7^\2\2\u034a\u00c1\3\2\2\2\u034b\u034d\7I\2\2\u034c\u034e"+
		"\t\2\2\2\u034d\u034c\3\2\2\2\u034d\u034e\3\2\2\2\u034e\u034f\3\2\2\2\u034f"+
		"\u0350\7^\2\2\u0350\u00c3\3\2\2\2\u0351\u0353\7J\2\2\u0352\u0354\t\2\2"+
		"\2\u0353\u0352\3\2\2\2\u0353\u0354\3\2\2\2\u0354\u0355\3\2\2\2\u0355\u0356"+
		"\7^\2\2\u0356\u00c5\3\2\2\2\u0357\u0359\7K\2\2\u0358\u035a\t\2\2\2\u0359"+
		"\u0358\3\2\2\2\u0359\u035a\3\2\2\2\u035a\u035b\3\2\2\2\u035b\u035c\7^"+
		"\2\2\u035c\u00c7\3\2\2\2\u035d\u035f\7L\2\2\u035e\u0360\t\2\2\2\u035f"+
		"\u035e\3\2\2\2\u035f\u0360\3\2\2\2\u0360\u0361\3\2\2\2\u0361\u0362\7^"+
		"\2\2\u0362\u00c9\3\2\2\2\u0363\u0365\7M\2\2\u0364\u0366\t\2\2\2\u0365"+
		"\u0364\3\2\2\2\u0365\u0366\3\2\2\2\u0366\u0367\3\2\2\2\u0367\u0368\7^"+
		"\2\2\u0368\u00cb\3\2\2\2\u0369\u036b\7N\2\2\u036a\u036c\t\2\2\2\u036b"+
		"\u036a\3\2\2\2\u036b\u036c\3\2\2\2\u036c\u036d\3\2\2\2\u036d\u036e\7^"+
		"\2\2\u036e\u00cd\3\2\2\2\u036f\u0374\7O\2\2\u0370\u0371\t\2\2\2\u0371"+
		"\u0372\t\2\2\2\u0372\u0373\t\2\2\2\u0373\u0375\t\2\2\2\u0374\u0370\3\2"+
		"\2\2\u0374\u0375\3\2\2\2\u0375\u0376\3\2\2\2\u0376\u0377\7^\2\2\u0377"+
		"\u00cf\3\2\2\2\u0378\u037d\7P\2\2\u0379\u037a\t\2\2\2\u037a\u037b\t\2"+
		"\2\2\u037b\u037c\t\2\2\2\u037c\u037e\t\2\2\2\u037d\u0379\3\2\2\2\u037d"+
		"\u037e\3\2\2\2\u037e\u037f\3\2\2\2\u037f\u0380\7^\2\2\u0380\u00d1\3\2"+
		"\2\2\u0381\u0388\7Q\2\2\u0382\u0383\t\2\2\2\u0383\u0384\t\2\2\2\u0384"+
		"\u0386\t\2\2\2\u0385\u0387\t\2\2\2\u0386\u0385\3\2\2\2\u0386\u0387\3\2"+
		"\2\2\u0387\u0389\3\2\2\2\u0388\u0382\3\2\2\2\u0388\u0389\3\2\2\2\u0389"+
		"\u038a\3\2\2\2\u038a\u038b\7^\2\2\u038b\u00d3\3\2\2\2\u038c\u0393\7R\2"+
		"\2\u038d\u038e\t\2\2\2\u038e\u038f\t\2\2\2\u038f\u0391\t\2\2\2\u0390\u0392"+
		"\t\2\2\2\u0391\u0390\3\2\2\2\u0391\u0392\3\2\2\2\u0392\u0394\3\2\2\2\u0393"+
		"\u038d\3\2\2\2\u0393\u0394\3\2\2\2\u0394\u0395\3\2\2\2\u0395\u0396\7^"+
		"\2\2\u0396\u00d5\3\2\2\2\u0397\u03a7\7S\2\2\u0398\u0399\t\2\2\2\u0399"+
		"\u039a\t\2\2\2\u039a\u039c\t\2\2\2\u039b\u039d\t\2\2\2\u039c\u039b\3\2"+
		"\2\2\u039c\u039d\3\2\2\2\u039d\u039f\3\2\2\2\u039e\u03a0\t\2\2\2\u039f"+
		"\u039e\3\2\2\2\u039f\u03a0\3\2\2\2\u03a0\u03a2\3\2\2\2\u03a1\u03a3\t\2"+
		"\2\2\u03a2\u03a1\3\2\2\2\u03a2\u03a3\3\2\2\2\u03a3\u03a5\3\2\2\2\u03a4"+
		"\u03a6\t\2\2\2\u03a5\u03a4\3\2\2\2\u03a5\u03a6\3\2\2\2\u03a6\u03a8\3\2"+
		"\2\2\u03a7\u0398\3\2\2\2\u03a7\u03a8\3\2\2\2\u03a8\u03a9\3\2\2\2\u03a9"+
		"\u03aa\7^\2\2\u03aa\u00d7\3\2\2\2\u03ab\u03bb\7T\2\2\u03ac\u03ad\t\2\2"+
		"\2\u03ad\u03ae\t\2\2\2\u03ae\u03b0\t\2\2\2\u03af\u03b1\t\2\2\2\u03b0\u03af"+
		"\3\2\2\2\u03b0\u03b1\3\2\2\2\u03b1\u03b3\3\2\2\2\u03b2\u03b4\t\2\2\2\u03b3"+
		"\u03b2\3\2\2\2\u03b3\u03b4\3\2\2\2\u03b4\u03b6\3\2\2\2\u03b5\u03b7\t\2"+
		"\2\2\u03b6\u03b5\3\2\2\2\u03b6\u03b7\3\2\2\2\u03b7\u03b9\3\2\2\2\u03b8"+
		"\u03ba\t\2\2\2\u03b9\u03b8\3\2\2\2\u03b9\u03ba\3\2\2\2\u03ba\u03bc\3\2"+
		"\2\2\u03bb\u03ac\3\2\2\2\u03bb\u03bc\3\2\2\2\u03bc\u03bd\3\2\2\2\u03bd"+
		"\u03be\7^\2\2\u03be\u00d9\3\2\2\2\u03bf\u03c9\7U\2\2\u03c0\u03c1\t\2\2"+
		"\2\u03c1\u03c2\t\2\2\2\u03c2\u03c4\t\2\2\2\u03c3\u03c5\t\2\2\2\u03c4\u03c3"+
		"\3\2\2\2\u03c4\u03c5\3\2\2\2\u03c5\u03c7\3\2\2\2\u03c6\u03c8\t\2\2\2\u03c7"+
		"\u03c6\3\2\2\2\u03c7\u03c8\3\2\2\2\u03c8\u03ca\3\2\2\2\u03c9\u03c0\3\2"+
		"\2\2\u03c9\u03ca\3\2\2\2\u03ca\u03cb\3\2\2\2\u03cb\u03cc\7^\2\2\u03cc"+
		"\u00db\3\2\2\2\u03cd\u03d7\7V\2\2\u03ce\u03cf\t\2\2\2\u03cf\u03d0\t\2"+
		"\2\2\u03d0\u03d2\t\2\2\2\u03d1\u03d3\t\2\2\2\u03d2\u03d1\3\2\2\2\u03d2"+
		"\u03d3\3\2\2\2\u03d3\u03d5\3\2\2\2\u03d4\u03d6\t\2\2\2\u03d5\u03d4\3\2"+
		"\2\2\u03d5\u03d6\3\2\2\2\u03d6\u03d8\3\2\2\2\u03d7\u03ce\3\2\2\2\u03d7"+
		"\u03d8\3\2\2\2\u03d8\u03d9\3\2\2\2\u03d9\u03da\7^\2\2\u03da\u00dd\3\2"+
		"\2\2\u03db\u03ee\7W\2\2\u03dc\u03dd\t\2\2\2\u03dd\u03de\t\2\2\2\u03de"+
		"\u03e0\t\2\2\2\u03df\u03e1\t\2\2\2\u03e0\u03df\3\2\2\2\u03e0\u03e1\3\2"+
		"\2\2\u03e1\u03e3\3\2\2\2\u03e2\u03e4\t\2\2\2\u03e3\u03e2\3\2\2\2\u03e3"+
		"\u03e4\3\2\2\2\u03e4\u03e6\3\2\2\2\u03e5\u03e7\t\2\2\2\u03e6\u03e5\3\2"+
		"\2\2\u03e6\u03e7\3\2\2\2\u03e7\u03e9\3\2\2\2\u03e8\u03ea\t\2\2\2\u03e9"+
		"\u03e8\3\2\2\2\u03e9\u03ea\3\2\2\2\u03ea\u03ec\3\2\2\2\u03eb\u03ed\t\2"+
		"\2\2\u03ec\u03eb\3\2\2\2\u03ec\u03ed\3\2\2\2\u03ed\u03ef\3\2\2\2\u03ee"+
		"\u03dc\3\2\2\2\u03ee\u03ef\3\2\2\2\u03ef\u03f0\3\2\2\2\u03f0\u03f1\7^"+
		"\2\2\u03f1\u00df\3\2\2\2\u03f2\u0405\7X\2\2\u03f3\u03f4\t\2\2\2\u03f4"+
		"\u03f5\t\2\2\2\u03f5\u03f7\t\2\2\2\u03f6\u03f8\t\2\2\2\u03f7\u03f6\3\2"+
		"\2\2\u03f7\u03f8\3\2\2\2\u03f8\u03fa\3\2\2\2\u03f9\u03fb\t\2\2\2\u03fa"+
		"\u03f9\3\2\2\2\u03fa\u03fb\3\2\2\2\u03fb\u03fd\3\2\2\2\u03fc\u03fe\t\2"+
		"\2\2\u03fd\u03fc\3\2\2\2\u03fd\u03fe\3\2\2\2\u03fe\u0400\3\2\2\2\u03ff"+
		"\u0401\t\2\2\2\u0400\u03ff\3\2\2\2\u0400\u0401\3\2\2\2\u0401\u0403\3\2"+
		"\2\2\u0402\u0404\t\2\2\2\u0403\u0402\3\2\2\2\u0403\u0404\3\2\2\2\u0404"+
		"\u0406\3\2\2\2\u0405\u03f3\3\2\2\2\u0405\u0406\3\2\2\2\u0406\u0407\3\2"+
		"\2\2\u0407\u0408\7^\2\2\u0408\u00e1\3\2\2\2\u0409\u040e\7Y\2\2\u040a\u040b"+
		"\t\2\2\2\u040b\u040c\t\2\2\2\u040c\u040d\t\2\2\2\u040d\u040f\t\2\2\2\u040e"+
		"\u040a\3\2\2\2\u040e\u040f\3\2\2\2\u040f\u0410\3\2\2\2\u0410\u0411\7^"+
		"\2\2\u0411\u00e3\3\2\2\2\u0412\u0414\7Z\2\2\u0413\u0415\t\2\2\2\u0414"+
		"\u0413\3\2\2\2\u0414\u0415\3\2\2\2\u0415\u0416\3\2\2\2\u0416\u0417\7^"+
		"\2\2\u0417\u00e5\3\2\2\2\u0418\u041d\7[\2\2\u0419\u041a\t\2\2\2\u041a"+
		"\u041b\t\2\2\2\u041b\u041c\t\2\2\2\u041c\u041e\t\2\2\2\u041d\u0419\3\2"+
		"\2\2\u041d\u041e\3\2\2\2\u041e\u041f\3\2\2\2\u041f\u0420\7^\2\2\u0420"+
		"\u00e7\3\2\2\2\u0421\u0423\7\\\2\2\u0422\u0424\t\2\2\2\u0423\u0422\3\2"+
		"\2\2\u0423\u0424\3\2\2\2\u0424\u0425\3\2\2\2\u0425\u0426\7^\2\2\u0426"+
		"\u00e9\3\2\2\2\u008f\u00ed\u00f1\u00f7\u00fe\u0121\u0124\u0129\u0138\u013c"+
		"\u0140\u0144\u0148\u0155\u015a\u015e\u0162\u0166\u016a\u016e\u0173\u0177"+
		"\u017b\u0185\u0189\u018d\u0193\u0195\u01a1\u01a6\u01aa\u01ae\u01b2\u01b8"+
		"\u01be\u01c4\u01ca\u01d0\u01d6\u01dc\u01e2\u01e8\u01ee\u01f4\u01fa\u0200"+
		"\u0206\u020c\u0212\u0218\u021e\u0224\u022a\u0230\u0236\u023d\u0244\u024b"+
		"\u0252\u0259\u025f\u0265\u026b\u0271\u0277\u027e\u0284\u028a\u0290\u0296"+
		"\u029c\u02a2\u02a8\u02ae\u02b4\u02ba\u02c2\u02ca\u02d0\u02d6\u02dc\u02e2"+
		"\u02e8\u02ee\u02f4\u02fa\u0300\u0308\u0310\u0316\u031c\u0322\u0328\u032e"+
		"\u0334\u033a\u0340\u0347\u034d\u0353\u0359\u035f\u0365\u036b\u0374\u037d"+
		"\u0386\u0388\u0391\u0393\u039c\u039f\u03a2\u03a5\u03a7\u03b0\u03b3\u03b6"+
		"\u03b9\u03bb\u03c4\u03c7\u03c9\u03d2\u03d5\u03d7\u03e0\u03e3\u03e6\u03e9"+
		"\u03ec\u03ee\u03f7\u03fa\u03fd\u0400\u0403\u0405\u040e\u0414\u041d\u0423";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}