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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, VERSION=6, UEBERTRAGUNGSFORMAT=7, 
		DATENBASIS=8, DATENBASIS_S=9, NETZKENNUNG=10, ERZEUGER=11, STAAT_DER_MESSSTELLE_LANG=12, 
		STAAT_DER_MESSSTELLE_KURZ=13, STAAT_DER_MESSSTELLE_S=14, MESSSTELLE=15, 
		MESSLABOR=16, PROBE_ID=17, MESSUNGS_ID=18, PROBEN_NR=19, HAUPTPROBENNUMMER=20, 
		NEBENPROBENNUMMER=21, MESSPROGRAMM_C=22, MESSPROGRAMM_S=23, MESSPROGRAMM_LAND=24, 
		PROBENAHMEINSTITUTION=25, PROBENART=26, ZEITBASIS=27, ZEITBASIS_S=28, 
		SOLL_DATUM_UHRZEIT_A=29, SOLL_DATUM_UHRZEIT_E=30, URSPRUNGS_DATUM_UHRZEIT=31, 
		PROBENAHME_DATUM_UHRZEIT_A=32, PROBENAHME_DATUM_UHRZEIT_E=33, UMWELTBEREICH_C=34, 
		UMWELTBEREICH_S=35, DESKRIPTOREN=36, REI_PROGRAMMPUNKT=37, REI_PROGRAMMPUNKTGRUPPE=38, 
		REFERENZ_DATUM_UHRZEIT=39, TESTDATEN=40, SZENARIO=41, SEK_DATENBASIS=42, 
		SEK_DATENBASIS_S=43, U_HERKUNFTSLAND_LANG=44, U_HERKUNFTSLAND_KURZ=45, 
		U_HERKUNFTSLAND_S=46, U_GEMEINDESCHLUESSEL=47, U_GEMEINDENAME=48, U_ORTS_ZUSATZKENNZAHL=49, 
		U_KOORDINATEN=50, U_KOORDINATEN_S=51, U_ORTS_ZUSATZCODE=52, U_ORTS_ZUSATZTEXT=53, 
		U_NUTS_CODE=54, P_HERKUNFTSLAND_LANG=55, P_HERKUNFTSLAND_KURZ=56, P_HERKUNFTSLAND_S=57, 
		P_GEMEINDESCHLUESSEL=58, P_GEMEINDENAME=59, P_ORTS_ZUSATZKENNZAHL=60, 
		P_KOORDINATEN=61, P_KOORDINATEN_S=62, P_ORTS_ZUSATZCODE=63, P_ORTS_ZUSATZTEXT=64, 
		P_NUTS_CODE=65, P_SITE_ID=66, P_SITE_NAME=67, P_HOEHE_NN=68, P_HOEHE_LAND=69, 
		MEHRZWECKFELD=70, MESS_DATUM_UHRZEIT=71, MESSZEIT_SEKUNDEN=72, MESSMETHODE_C=73, 
		MESSMETHODE_S=74, BEARBEITUNGSSTATUS=75, PEP_FLAG=76, ERFASSUNG_ABGESCHLOSSEN=77, 
		PROBENZUSATZBESCHREIBUNG=78, PZB_S=79, MESSWERT=80, MESSWERT_S=81, MESSWERT_I=82, 
		MESSWERT_G=83, MESSWERT_NWG=84, MESSWERT_NWG_S=85, MESSWERT_NWG_I=86, 
		MESSWERT_NWG_G=87, KOMMENTAR=88, KOMMENTAR_T=89, PROBENKOMMENTAR=90, PROBENKOMMENTAR_T=91, 
		WS=92, NEWLINE=93, STRING_ESC=94, STRING=95, C=96;
	public static final int
		RULE_probendatei = 0, RULE_end = 1, RULE_probe = 2, RULE_probedaten = 3, 
		RULE_messung = 4, RULE_mess_header = 5, RULE_messungdaten = 6, RULE_db = 7, 
		RULE_mp = 8, RULE_ub = 9, RULE_rei = 10, RULE_ph = 11, RULE_pg = 12, RULE_pk = 13, 
		RULE_zb = 14, RULE_pzb = 15, RULE_pkom = 16, RULE_sdm = 17, RULE_pnh = 18, 
		RULE_mm = 19, RULE_mw = 20, RULE_mess_kommentar = 21, RULE_pn = 22, RULE_us = 23, 
		RULE_ursprungsortdaten = 24, RULE_uh = 25, RULE_ug = 26, RULE_uk = 27, 
		RULE_version = 28, RULE_uebertragungsformat = 29, RULE_datenbasis = 30, 
		RULE_datenbasis_s = 31, RULE_netzkennung = 32, RULE_erzeuger = 33, RULE_staat_der_messstelle_lang = 34, 
		RULE_staat_der_messstelle_kurz = 35, RULE_staat_der_messstelle_s = 36, 
		RULE_messstelle = 37, RULE_messlabor = 38, RULE_probe_id = 39, RULE_messungs_id = 40, 
		RULE_proben_nr = 41, RULE_hauptprobennummer = 42, RULE_nebenprobennummer = 43, 
		RULE_messprogramm_c = 44, RULE_messprogramm_s = 45, RULE_messprogramm_land = 46, 
		RULE_probenahmeinstitution = 47, RULE_probenart = 48, RULE_zeitbasis = 49, 
		RULE_zeitbasis_s = 50, RULE_soll_datum_uhrzeit_a = 51, RULE_soll_datum_uhrzeit_e = 52, 
		RULE_ursprungs_datum_uhrzeit = 53, RULE_probenahme_datum_uhrzeit_a = 54, 
		RULE_probenahme_datum_uhrzeit_e = 55, RULE_umweltbereich_c = 56, RULE_umweltbereich_s = 57, 
		RULE_deskriptoren = 58, RULE_rei_programmpunkt = 59, RULE_rei_programmpunktgruppe = 60, 
		RULE_referenz_datum_uhrzeit = 61, RULE_testdaten = 62, RULE_szenario = 63, 
		RULE_sek_datenbasis = 64, RULE_sek_datenbasis_s = 65, RULE_u_herkunftsland_lang = 66, 
		RULE_u_herkunftsland_kurz = 67, RULE_u_herkunftsland_s = 68, RULE_u_gemeindeschluessel = 69, 
		RULE_u_gemeindename = 70, RULE_u_orts_zusatzkennzahl = 71, RULE_u_koordinaten = 72, 
		RULE_u_koordinaten_s = 73, RULE_u_orts_zusatzcode = 74, RULE_u_orts_zusatztext = 75, 
		RULE_u_nuts_code = 76, RULE_p_herkunftsland_lang = 77, RULE_p_herkunftsland_kurz = 78, 
		RULE_p_herkunftsland_s = 79, RULE_p_gemeindeschluessel = 80, RULE_p_gemeindename = 81, 
		RULE_p_orts_zusatzkennzahl = 82, RULE_p_koordinaten = 83, RULE_p_koordinaten_s = 84, 
		RULE_p_orts_zusatzcode = 85, RULE_p_orts_zusatztext = 86, RULE_p_nuts_code = 87, 
		RULE_p_site_id = 88, RULE_p_site_name = 89, RULE_p_hoehe_nn = 90, RULE_p_hoehe_land = 91, 
		RULE_mehrzweckfeld = 92, RULE_mess_datum_uhrzeit = 93, RULE_messzeit_sekunden = 94, 
		RULE_messmethode_c = 95, RULE_messmethode_s = 96, RULE_bearbeitungsstatus = 97, 
		RULE_pep_flag = 98, RULE_erfassung_abgeschlossen = 99, RULE_probenzusatzbeschreibung = 100, 
		RULE_pzb_s = 101, RULE_messwert = 102, RULE_messwert_s = 103, RULE_messwert_i = 104, 
		RULE_messwert_g = 105, RULE_messwert_nwg = 106, RULE_messwert_nwg_s = 107, 
		RULE_messwert_nwg_i = 108, RULE_messwert_nwg_g = 109, RULE_kommentar = 110, 
		RULE_kommentar_t = 111, RULE_probenkommentar = 112, RULE_probenkommentar_t = 113;
	public static final String[] ruleNames = {
		"probendatei", "end", "probe", "probedaten", "messung", "mess_header", 
		"messungdaten", "db", "mp", "ub", "rei", "ph", "pg", "pk", "zb", "pzb", 
		"pkom", "sdm", "pnh", "mm", "mw", "mess_kommentar", "pn", "us", "ursprungsortdaten", 
		"uh", "ug", "uk", "version", "uebertragungsformat", "datenbasis", "datenbasis_s", 
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
		null, "'%ENDE%'", "'%PROBE%'", "'%MESSUNG%'", "'%URSPRUNGSORT%'", "' '"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "VERSION", "UEBERTRAGUNGSFORMAT", 
		"DATENBASIS", "DATENBASIS_S", "NETZKENNUNG", "ERZEUGER", "STAAT_DER_MESSSTELLE_LANG", 
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
			setState(231);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(228);
				probe();
				}
				}
				setState(233);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(235);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(234);
				end();
				}
			}

			setState(237);
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(239);
			match(T__0);
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
			setState(241);
			match(T__1);
			setState(243); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(242);
				probedaten();
				}
				}
				setState(245); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << VERSION) | (1L << UEBERTRAGUNGSFORMAT) | (1L << DATENBASIS) | (1L << DATENBASIS_S) | (1L << NETZKENNUNG) | (1L << ERZEUGER) | (1L << STAAT_DER_MESSSTELLE_LANG) | (1L << STAAT_DER_MESSSTELLE_KURZ) | (1L << STAAT_DER_MESSSTELLE_S) | (1L << MESSSTELLE) | (1L << MESSLABOR) | (1L << PROBE_ID) | (1L << MESSUNGS_ID) | (1L << PROBEN_NR) | (1L << HAUPTPROBENNUMMER) | (1L << NEBENPROBENNUMMER) | (1L << MESSPROGRAMM_C) | (1L << MESSPROGRAMM_S) | (1L << MESSPROGRAMM_LAND) | (1L << PROBENAHMEINSTITUTION) | (1L << PROBENART) | (1L << ZEITBASIS) | (1L << ZEITBASIS_S) | (1L << SOLL_DATUM_UHRZEIT_A) | (1L << SOLL_DATUM_UHRZEIT_E) | (1L << PROBENAHME_DATUM_UHRZEIT_A) | (1L << PROBENAHME_DATUM_UHRZEIT_E) | (1L << UMWELTBEREICH_C) | (1L << UMWELTBEREICH_S) | (1L << DESKRIPTOREN) | (1L << REI_PROGRAMMPUNKT) | (1L << REI_PROGRAMMPUNKTGRUPPE) | (1L << REFERENZ_DATUM_UHRZEIT) | (1L << TESTDATEN) | (1L << SZENARIO) | (1L << SEK_DATENBASIS) | (1L << SEK_DATENBASIS_S) | (1L << P_HERKUNFTSLAND_LANG) | (1L << P_HERKUNFTSLAND_KURZ) | (1L << P_HERKUNFTSLAND_S) | (1L << P_GEMEINDESCHLUESSEL) | (1L << P_GEMEINDENAME) | (1L << P_ORTS_ZUSATZKENNZAHL) | (1L << P_KOORDINATEN) | (1L << P_KOORDINATEN_S) | (1L << P_ORTS_ZUSATZCODE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (P_ORTS_ZUSATZTEXT - 64)) | (1L << (P_NUTS_CODE - 64)) | (1L << (P_SITE_ID - 64)) | (1L << (P_SITE_NAME - 64)) | (1L << (P_HOEHE_NN - 64)) | (1L << (P_HOEHE_LAND - 64)) | (1L << (MEHRZWECKFELD - 64)) | (1L << (MESS_DATUM_UHRZEIT - 64)) | (1L << (MESSZEIT_SEKUNDEN - 64)) | (1L << (MESSMETHODE_C - 64)) | (1L << (MESSMETHODE_S - 64)) | (1L << (BEARBEITUNGSSTATUS - 64)) | (1L << (PEP_FLAG - 64)) | (1L << (ERFASSUNG_ABGESCHLOSSEN - 64)) | (1L << (PROBENZUSATZBESCHREIBUNG - 64)) | (1L << (PZB_S - 64)) | (1L << (MESSWERT - 64)) | (1L << (MESSWERT_S - 64)) | (1L << (MESSWERT_I - 64)) | (1L << (MESSWERT_G - 64)) | (1L << (MESSWERT_NWG - 64)) | (1L << (MESSWERT_NWG_S - 64)) | (1L << (MESSWERT_NWG_I - 64)) | (1L << (MESSWERT_NWG_G - 64)) | (1L << (KOMMENTAR - 64)) | (1L << (KOMMENTAR_T - 64)) | (1L << (PROBENKOMMENTAR - 64)) | (1L << (PROBENKOMMENTAR_T - 64)))) != 0) );
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
		public UsContext us() {
			return getRuleContext(UsContext.class,0);
		}
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
			setState(290);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(247);
				db();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(248);
				version();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(249);
				uebertragungsformat();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(250);
				netzkennung();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(251);
				erzeuger();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(252);
				sdm();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(253);
				messstelle();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(254);
				messlabor();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(255);
				probe_id();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(256);
				pnh();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(257);
				mp();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(258);
				messprogramm_land();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(259);
				probenahmeinstitution();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(260);
				probenart();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(261);
				zb();
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(262);
				probenahme_datum_uhrzeit_a();
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(263);
				probenahme_datum_uhrzeit_e();
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(264);
				soll_datum_uhrzeit_a();
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(265);
				soll_datum_uhrzeit_e();
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(266);
				ub();
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(267);
				deskriptoren();
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(268);
				rei();
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(269);
				referenz_datum_uhrzeit();
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(270);
				testdaten();
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(271);
				szenario();
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				setState(272);
				sek_datenbasis();
				}
				break;
			case 27:
				enterOuterAlt(_localctx, 27);
				{
				setState(273);
				sek_datenbasis_s();
				}
				break;
			case 28:
				enterOuterAlt(_localctx, 28);
				{
				setState(274);
				us();
				}
				break;
			case 29:
				enterOuterAlt(_localctx, 29);
				{
				setState(275);
				ph();
				}
				break;
			case 30:
				enterOuterAlt(_localctx, 30);
				{
				setState(276);
				pg();
				}
				break;
			case 31:
				enterOuterAlt(_localctx, 31);
				{
				setState(277);
				p_orts_zusatzkennzahl();
				}
				break;
			case 32:
				enterOuterAlt(_localctx, 32);
				{
				setState(278);
				pk();
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 33);
				{
				setState(279);
				p_orts_zusatzcode();
				}
				break;
			case 34:
				enterOuterAlt(_localctx, 34);
				{
				setState(280);
				p_orts_zusatztext();
				}
				break;
			case 35:
				enterOuterAlt(_localctx, 35);
				{
				setState(281);
				p_nuts_code();
				}
				break;
			case 36:
				enterOuterAlt(_localctx, 36);
				{
				setState(282);
				p_site_id();
				}
				break;
			case 37:
				enterOuterAlt(_localctx, 37);
				{
				setState(283);
				p_site_name();
				}
				break;
			case 38:
				enterOuterAlt(_localctx, 38);
				{
				setState(284);
				p_hoehe_nn();
				}
				break;
			case 39:
				enterOuterAlt(_localctx, 39);
				{
				setState(285);
				p_hoehe_land();
				}
				break;
			case 40:
				enterOuterAlt(_localctx, 40);
				{
				setState(286);
				mehrzweckfeld();
				}
				break;
			case 41:
				enterOuterAlt(_localctx, 41);
				{
				setState(287);
				messung();
				}
				break;
			case 42:
				enterOuterAlt(_localctx, 42);
				{
				setState(288);
				pzb();
				}
				break;
			case 43:
				enterOuterAlt(_localctx, 43);
				{
				setState(289);
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
			setState(293);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(292);
				mess_header();
				}
			}

			setState(296); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(295);
					messungdaten();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(298); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
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
			setState(300);
			match(T__2);
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
			setState(312);
			switch (_input.LA(1)) {
			case MESSUNGS_ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(302);
				messungs_id();
				}
				break;
			case PROBEN_NR:
			case NEBENPROBENNUMMER:
				enterOuterAlt(_localctx, 2);
				{
				setState(303);
				pn();
				}
				break;
			case MESS_DATUM_UHRZEIT:
				enterOuterAlt(_localctx, 3);
				{
				setState(304);
				mess_datum_uhrzeit();
				}
				break;
			case MESSZEIT_SEKUNDEN:
				enterOuterAlt(_localctx, 4);
				{
				setState(305);
				messzeit_sekunden();
				}
				break;
			case MESSMETHODE_C:
			case MESSMETHODE_S:
				enterOuterAlt(_localctx, 5);
				{
				setState(306);
				mm();
				}
				break;
			case BEARBEITUNGSSTATUS:
				enterOuterAlt(_localctx, 6);
				{
				setState(307);
				bearbeitungsstatus();
				}
				break;
			case PEP_FLAG:
				enterOuterAlt(_localctx, 7);
				{
				setState(308);
				pep_flag();
				}
				break;
			case ERFASSUNG_ABGESCHLOSSEN:
				enterOuterAlt(_localctx, 8);
				{
				setState(309);
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
				setState(310);
				mw();
				}
				break;
			case KOMMENTAR:
			case KOMMENTAR_T:
				enterOuterAlt(_localctx, 10);
				{
				setState(311);
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
			setState(316);
			switch (_input.LA(1)) {
			case DATENBASIS:
				enterOuterAlt(_localctx, 1);
				{
				setState(314);
				datenbasis();
				}
				break;
			case DATENBASIS_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(315);
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
			setState(320);
			switch (_input.LA(1)) {
			case MESSPROGRAMM_C:
				enterOuterAlt(_localctx, 1);
				{
				setState(318);
				messprogramm_c();
				}
				break;
			case MESSPROGRAMM_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(319);
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
			setState(324);
			switch (_input.LA(1)) {
			case UMWELTBEREICH_C:
				enterOuterAlt(_localctx, 1);
				{
				setState(322);
				umweltbereich_c();
				}
				break;
			case UMWELTBEREICH_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(323);
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
			setState(328);
			switch (_input.LA(1)) {
			case REI_PROGRAMMPUNKT:
				enterOuterAlt(_localctx, 1);
				{
				setState(326);
				rei_programmpunkt();
				}
				break;
			case REI_PROGRAMMPUNKTGRUPPE:
				enterOuterAlt(_localctx, 2);
				{
				setState(327);
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
		enterRule(_localctx, 22, RULE_ph);
		try {
			setState(333);
			switch (_input.LA(1)) {
			case P_HERKUNFTSLAND_LANG:
				enterOuterAlt(_localctx, 1);
				{
				setState(330);
				p_herkunftsland_lang();
				}
				break;
			case P_HERKUNFTSLAND_KURZ:
				enterOuterAlt(_localctx, 2);
				{
				setState(331);
				p_herkunftsland_kurz();
				}
				break;
			case P_HERKUNFTSLAND_S:
				enterOuterAlt(_localctx, 3);
				{
				setState(332);
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
		enterRule(_localctx, 24, RULE_pg);
		try {
			setState(337);
			switch (_input.LA(1)) {
			case P_GEMEINDESCHLUESSEL:
				enterOuterAlt(_localctx, 1);
				{
				setState(335);
				p_gemeindeschluessel();
				}
				break;
			case P_GEMEINDENAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(336);
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
		enterRule(_localctx, 26, RULE_pk);
		try {
			setState(341);
			switch (_input.LA(1)) {
			case P_KOORDINATEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(339);
				p_koordinaten();
				}
				break;
			case P_KOORDINATEN_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(340);
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
		enterRule(_localctx, 28, RULE_zb);
		try {
			setState(345);
			switch (_input.LA(1)) {
			case ZEITBASIS:
				enterOuterAlt(_localctx, 1);
				{
				setState(343);
				zeitbasis();
				}
				break;
			case ZEITBASIS_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(344);
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
		enterRule(_localctx, 30, RULE_pzb);
		try {
			setState(349);
			switch (_input.LA(1)) {
			case PROBENZUSATZBESCHREIBUNG:
				enterOuterAlt(_localctx, 1);
				{
				setState(347);
				probenzusatzbeschreibung();
				}
				break;
			case PZB_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(348);
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
		enterRule(_localctx, 32, RULE_pkom);
		try {
			setState(353);
			switch (_input.LA(1)) {
			case PROBENKOMMENTAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(351);
				probenkommentar();
				}
				break;
			case PROBENKOMMENTAR_T:
				enterOuterAlt(_localctx, 2);
				{
				setState(352);
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
		enterRule(_localctx, 34, RULE_sdm);
		try {
			setState(358);
			switch (_input.LA(1)) {
			case STAAT_DER_MESSSTELLE_LANG:
				enterOuterAlt(_localctx, 1);
				{
				setState(355);
				staat_der_messstelle_lang();
				}
				break;
			case STAAT_DER_MESSSTELLE_KURZ:
				enterOuterAlt(_localctx, 2);
				{
				setState(356);
				staat_der_messstelle_kurz();
				}
				break;
			case STAAT_DER_MESSSTELLE_S:
				enterOuterAlt(_localctx, 3);
				{
				setState(357);
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
		enterRule(_localctx, 36, RULE_pnh);
		try {
			setState(362);
			switch (_input.LA(1)) {
			case PROBEN_NR:
				enterOuterAlt(_localctx, 1);
				{
				setState(360);
				proben_nr();
				}
				break;
			case HAUPTPROBENNUMMER:
				enterOuterAlt(_localctx, 2);
				{
				setState(361);
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
		enterRule(_localctx, 38, RULE_mm);
		try {
			setState(366);
			switch (_input.LA(1)) {
			case MESSMETHODE_C:
				enterOuterAlt(_localctx, 1);
				{
				setState(364);
				messmethode_c();
				}
				break;
			case MESSMETHODE_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(365);
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
		enterRule(_localctx, 40, RULE_mw);
		try {
			setState(376);
			switch (_input.LA(1)) {
			case MESSWERT:
				enterOuterAlt(_localctx, 1);
				{
				setState(368);
				messwert();
				}
				break;
			case MESSWERT_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(369);
				messwert_s();
				}
				break;
			case MESSWERT_I:
				enterOuterAlt(_localctx, 3);
				{
				setState(370);
				messwert_i();
				}
				break;
			case MESSWERT_G:
				enterOuterAlt(_localctx, 4);
				{
				setState(371);
				messwert_g();
				}
				break;
			case MESSWERT_NWG:
				enterOuterAlt(_localctx, 5);
				{
				setState(372);
				messwert_nwg();
				}
				break;
			case MESSWERT_NWG_S:
				enterOuterAlt(_localctx, 6);
				{
				setState(373);
				messwert_nwg_s();
				}
				break;
			case MESSWERT_NWG_I:
				enterOuterAlt(_localctx, 7);
				{
				setState(374);
				messwert_nwg_i();
				}
				break;
			case MESSWERT_NWG_G:
				enterOuterAlt(_localctx, 8);
				{
				setState(375);
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
		enterRule(_localctx, 42, RULE_mess_kommentar);
		try {
			setState(380);
			switch (_input.LA(1)) {
			case KOMMENTAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(378);
				kommentar();
				}
				break;
			case KOMMENTAR_T:
				enterOuterAlt(_localctx, 2);
				{
				setState(379);
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
		enterRule(_localctx, 44, RULE_pn);
		try {
			setState(384);
			switch (_input.LA(1)) {
			case PROBEN_NR:
				enterOuterAlt(_localctx, 1);
				{
				setState(382);
				proben_nr();
				}
				break;
			case NEBENPROBENNUMMER:
				enterOuterAlt(_localctx, 2);
				{
				setState(383);
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

	public static class UsContext extends ParserRuleContext {
		public List<UrsprungsortdatenContext> ursprungsortdaten() {
			return getRuleContexts(UrsprungsortdatenContext.class);
		}
		public UrsprungsortdatenContext ursprungsortdaten(int i) {
			return getRuleContext(UrsprungsortdatenContext.class,i);
		}
		public UsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_us; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).enterUs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LafListener ) ((LafListener)listener).exitUs(this);
		}
	}

	public final UsContext us() throws RecognitionException {
		UsContext _localctx = new UsContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_us);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(386);
			match(T__3);
			setState(390);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << U_HERKUNFTSLAND_LANG) | (1L << U_HERKUNFTSLAND_KURZ) | (1L << U_HERKUNFTSLAND_S) | (1L << U_GEMEINDESCHLUESSEL) | (1L << U_GEMEINDENAME) | (1L << U_ORTS_ZUSATZKENNZAHL) | (1L << U_KOORDINATEN) | (1L << U_KOORDINATEN_S) | (1L << U_ORTS_ZUSATZCODE) | (1L << U_ORTS_ZUSATZTEXT) | (1L << U_NUTS_CODE))) != 0)) {
				{
				{
				setState(387);
				ursprungsortdaten();
				}
				}
				setState(392);
				_errHandler.sync(this);
				_la = _input.LA(1);
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
		enterRule(_localctx, 48, RULE_ursprungsortdaten);
		try {
			setState(400);
			switch (_input.LA(1)) {
			case U_HERKUNFTSLAND_LANG:
			case U_HERKUNFTSLAND_KURZ:
			case U_HERKUNFTSLAND_S:
				enterOuterAlt(_localctx, 1);
				{
				setState(393);
				uh();
				}
				break;
			case U_GEMEINDESCHLUESSEL:
			case U_GEMEINDENAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(394);
				ug();
				}
				break;
			case U_ORTS_ZUSATZKENNZAHL:
				enterOuterAlt(_localctx, 3);
				{
				setState(395);
				u_orts_zusatzkennzahl();
				}
				break;
			case U_KOORDINATEN:
			case U_KOORDINATEN_S:
				enterOuterAlt(_localctx, 4);
				{
				setState(396);
				uk();
				}
				break;
			case U_ORTS_ZUSATZCODE:
				enterOuterAlt(_localctx, 5);
				{
				setState(397);
				u_orts_zusatzcode();
				}
				break;
			case U_ORTS_ZUSATZTEXT:
				enterOuterAlt(_localctx, 6);
				{
				setState(398);
				u_orts_zusatztext();
				}
				break;
			case U_NUTS_CODE:
				enterOuterAlt(_localctx, 7);
				{
				setState(399);
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
		enterRule(_localctx, 50, RULE_uh);
		try {
			setState(405);
			switch (_input.LA(1)) {
			case U_HERKUNFTSLAND_LANG:
				enterOuterAlt(_localctx, 1);
				{
				setState(402);
				u_herkunftsland_lang();
				}
				break;
			case U_HERKUNFTSLAND_KURZ:
				enterOuterAlt(_localctx, 2);
				{
				setState(403);
				u_herkunftsland_kurz();
				}
				break;
			case U_HERKUNFTSLAND_S:
				enterOuterAlt(_localctx, 3);
				{
				setState(404);
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
		enterRule(_localctx, 52, RULE_ug);
		try {
			setState(409);
			switch (_input.LA(1)) {
			case U_GEMEINDESCHLUESSEL:
				enterOuterAlt(_localctx, 1);
				{
				setState(407);
				u_gemeindeschluessel();
				}
				break;
			case U_GEMEINDENAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(408);
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
		enterRule(_localctx, 54, RULE_uk);
		try {
			setState(413);
			switch (_input.LA(1)) {
			case U_KOORDINATEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(411);
				u_koordinaten();
				}
				break;
			case U_KOORDINATEN_S:
				enterOuterAlt(_localctx, 2);
				{
				setState(412);
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
		enterRule(_localctx, 56, RULE_version);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(415);
			match(VERSION);
			setState(416);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class UebertragungsformatContext extends ParserRuleContext {
		public TerminalNode UEBERTRAGUNGSFORMAT() { return getToken(LafParser.UEBERTRAGUNGSFORMAT, 0); }
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
		enterRule(_localctx, 58, RULE_uebertragungsformat);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418);
			match(UEBERTRAGUNGSFORMAT);
			setState(419);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class DatenbasisContext extends ParserRuleContext {
		public TerminalNode DATENBASIS() { return getToken(LafParser.DATENBASIS, 0); }
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
		enterRule(_localctx, 60, RULE_datenbasis);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(421);
			match(DATENBASIS);
			setState(422);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Datenbasis_sContext extends ParserRuleContext {
		public TerminalNode DATENBASIS_S() { return getToken(LafParser.DATENBASIS_S, 0); }
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
		enterRule(_localctx, 62, RULE_datenbasis_s);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(424);
			match(DATENBASIS_S);
			setState(425);
			match(STRING);
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
		enterRule(_localctx, 64, RULE_netzkennung);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(427);
			match(NETZKENNUNG);
			setState(428);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class ErzeugerContext extends ParserRuleContext {
		public TerminalNode ERZEUGER() { return getToken(LafParser.ERZEUGER, 0); }
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
		enterRule(_localctx, 66, RULE_erzeuger);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(430);
			match(ERZEUGER);
			setState(431);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Staat_der_messstelle_langContext extends ParserRuleContext {
		public TerminalNode STAAT_DER_MESSSTELLE_LANG() { return getToken(LafParser.STAAT_DER_MESSSTELLE_LANG, 0); }
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
		enterRule(_localctx, 68, RULE_staat_der_messstelle_lang);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(433);
			match(STAAT_DER_MESSSTELLE_LANG);
			setState(434);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Staat_der_messstelle_kurzContext extends ParserRuleContext {
		public TerminalNode STAAT_DER_MESSSTELLE_KURZ() { return getToken(LafParser.STAAT_DER_MESSSTELLE_KURZ, 0); }
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
		enterRule(_localctx, 70, RULE_staat_der_messstelle_kurz);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(436);
			match(STAAT_DER_MESSSTELLE_KURZ);
			setState(437);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Staat_der_messstelle_sContext extends ParserRuleContext {
		public TerminalNode STAAT_DER_MESSSTELLE_S() { return getToken(LafParser.STAAT_DER_MESSSTELLE_S, 0); }
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
		enterRule(_localctx, 72, RULE_staat_der_messstelle_s);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(439);
			match(STAAT_DER_MESSSTELLE_S);
			setState(440);
			match(STRING);
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
		enterRule(_localctx, 74, RULE_messstelle);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(442);
			match(MESSSTELLE);
			setState(443);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class MesslaborContext extends ParserRuleContext {
		public TerminalNode MESSLABOR() { return getToken(LafParser.MESSLABOR, 0); }
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
		enterRule(_localctx, 76, RULE_messlabor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(445);
			match(MESSLABOR);
			setState(446);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Probe_idContext extends ParserRuleContext {
		public TerminalNode PROBE_ID() { return getToken(LafParser.PROBE_ID, 0); }
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
		enterRule(_localctx, 78, RULE_probe_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(448);
			match(PROBE_ID);
			setState(449);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Messungs_idContext extends ParserRuleContext {
		public TerminalNode MESSUNGS_ID() { return getToken(LafParser.MESSUNGS_ID, 0); }
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
		enterRule(_localctx, 80, RULE_messungs_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(451);
			match(MESSUNGS_ID);
			setState(452);
			match(STRING);
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
		enterRule(_localctx, 82, RULE_proben_nr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(454);
			match(PROBEN_NR);
			setState(455);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class HauptprobennummerContext extends ParserRuleContext {
		public TerminalNode HAUPTPROBENNUMMER() { return getToken(LafParser.HAUPTPROBENNUMMER, 0); }
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
		enterRule(_localctx, 84, RULE_hauptprobennummer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(457);
			match(HAUPTPROBENNUMMER);
			setState(458);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class NebenprobennummerContext extends ParserRuleContext {
		public TerminalNode NEBENPROBENNUMMER() { return getToken(LafParser.NEBENPROBENNUMMER, 0); }
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
		enterRule(_localctx, 86, RULE_nebenprobennummer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(460);
			match(NEBENPROBENNUMMER);
			setState(461);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Messprogramm_cContext extends ParserRuleContext {
		public TerminalNode MESSPROGRAMM_C() { return getToken(LafParser.MESSPROGRAMM_C, 0); }
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
		enterRule(_localctx, 88, RULE_messprogramm_c);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(463);
			match(MESSPROGRAMM_C);
			setState(464);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Messprogramm_sContext extends ParserRuleContext {
		public TerminalNode MESSPROGRAMM_S() { return getToken(LafParser.MESSPROGRAMM_S, 0); }
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
		enterRule(_localctx, 90, RULE_messprogramm_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(466);
			match(MESSPROGRAMM_S);
			setState(467);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Messprogramm_landContext extends ParserRuleContext {
		public TerminalNode MESSPROGRAMM_LAND() { return getToken(LafParser.MESSPROGRAMM_LAND, 0); }
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
		enterRule(_localctx, 92, RULE_messprogramm_land);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(469);
			match(MESSPROGRAMM_LAND);
			setState(470);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class ProbenahmeinstitutionContext extends ParserRuleContext {
		public TerminalNode PROBENAHMEINSTITUTION() { return getToken(LafParser.PROBENAHMEINSTITUTION, 0); }
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
		enterRule(_localctx, 94, RULE_probenahmeinstitution);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(472);
			match(PROBENAHMEINSTITUTION);
			setState(473);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class ProbenartContext extends ParserRuleContext {
		public TerminalNode PROBENART() { return getToken(LafParser.PROBENART, 0); }
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
		enterRule(_localctx, 96, RULE_probenart);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(475);
			match(PROBENART);
			setState(476);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class ZeitbasisContext extends ParserRuleContext {
		public TerminalNode ZEITBASIS() { return getToken(LafParser.ZEITBASIS, 0); }
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
		enterRule(_localctx, 98, RULE_zeitbasis);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(478);
			match(ZEITBASIS);
			setState(479);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Zeitbasis_sContext extends ParserRuleContext {
		public TerminalNode ZEITBASIS_S() { return getToken(LafParser.ZEITBASIS_S, 0); }
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
		enterRule(_localctx, 100, RULE_zeitbasis_s);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(481);
			match(ZEITBASIS_S);
			setState(482);
			match(STRING);
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
		enterRule(_localctx, 102, RULE_soll_datum_uhrzeit_a);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(484);
			match(SOLL_DATUM_UHRZEIT_A);
			setState(485);
			match(STRING);
			setState(489);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(486);
				match(T__4);
				}
				}
				setState(491);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(492);
			match(STRING);
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
		enterRule(_localctx, 104, RULE_soll_datum_uhrzeit_e);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(494);
			match(SOLL_DATUM_UHRZEIT_E);
			setState(495);
			match(STRING);
			setState(499);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(496);
				match(T__4);
				}
				}
				setState(501);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(502);
			match(STRING);
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
		enterRule(_localctx, 106, RULE_ursprungs_datum_uhrzeit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(504);
			match(URSPRUNGS_DATUM_UHRZEIT);
			setState(505);
			match(STRING);
			setState(509);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(506);
				match(T__4);
				}
				}
				setState(511);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(512);
			match(STRING);
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
		enterRule(_localctx, 108, RULE_probenahme_datum_uhrzeit_a);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(514);
			match(PROBENAHME_DATUM_UHRZEIT_A);
			setState(515);
			match(STRING);
			setState(519);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(516);
				match(T__4);
				}
				}
				setState(521);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(522);
			match(STRING);
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
		enterRule(_localctx, 110, RULE_probenahme_datum_uhrzeit_e);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(524);
			match(PROBENAHME_DATUM_UHRZEIT_E);
			setState(525);
			match(STRING);
			setState(529);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(526);
				match(T__4);
				}
				}
				setState(531);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(532);
			match(STRING);
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
		enterRule(_localctx, 112, RULE_umweltbereich_c);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(534);
			match(UMWELTBEREICH_C);
			setState(535);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Umweltbereich_sContext extends ParserRuleContext {
		public TerminalNode UMWELTBEREICH_S() { return getToken(LafParser.UMWELTBEREICH_S, 0); }
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
		enterRule(_localctx, 114, RULE_umweltbereich_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(537);
			match(UMWELTBEREICH_S);
			setState(538);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class DeskriptorenContext extends ParserRuleContext {
		public TerminalNode DESKRIPTOREN() { return getToken(LafParser.DESKRIPTOREN, 0); }
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
		enterRule(_localctx, 116, RULE_deskriptoren);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(540);
			match(DESKRIPTOREN);
			setState(541);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Rei_programmpunktContext extends ParserRuleContext {
		public TerminalNode REI_PROGRAMMPUNKT() { return getToken(LafParser.REI_PROGRAMMPUNKT, 0); }
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
		enterRule(_localctx, 118, RULE_rei_programmpunkt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(543);
			match(REI_PROGRAMMPUNKT);
			setState(544);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Rei_programmpunktgruppeContext extends ParserRuleContext {
		public TerminalNode REI_PROGRAMMPUNKTGRUPPE() { return getToken(LafParser.REI_PROGRAMMPUNKTGRUPPE, 0); }
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
		enterRule(_localctx, 120, RULE_rei_programmpunktgruppe);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(546);
			match(REI_PROGRAMMPUNKTGRUPPE);
			setState(547);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Referenz_datum_uhrzeitContext extends ParserRuleContext {
		public TerminalNode REFERENZ_DATUM_UHRZEIT() { return getToken(LafParser.REFERENZ_DATUM_UHRZEIT, 0); }
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
		enterRule(_localctx, 122, RULE_referenz_datum_uhrzeit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(549);
			match(REFERENZ_DATUM_UHRZEIT);
			setState(550);
			match(STRING);
			setState(554);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(551);
				match(T__4);
				}
				}
				setState(556);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(557);
			match(STRING);
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
		enterRule(_localctx, 124, RULE_testdaten);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(559);
			match(TESTDATEN);
			setState(560);
			match(STRING);
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
		enterRule(_localctx, 126, RULE_szenario);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(562);
			match(SZENARIO);
			setState(563);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Sek_datenbasisContext extends ParserRuleContext {
		public TerminalNode SEK_DATENBASIS() { return getToken(LafParser.SEK_DATENBASIS, 0); }
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
		enterRule(_localctx, 128, RULE_sek_datenbasis);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(565);
			match(SEK_DATENBASIS);
			setState(566);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Sek_datenbasis_sContext extends ParserRuleContext {
		public TerminalNode SEK_DATENBASIS_S() { return getToken(LafParser.SEK_DATENBASIS_S, 0); }
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
		enterRule(_localctx, 130, RULE_sek_datenbasis_s);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(568);
			match(SEK_DATENBASIS_S);
			setState(569);
			match(STRING);
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
		enterRule(_localctx, 132, RULE_u_herkunftsland_lang);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(571);
			match(U_HERKUNFTSLAND_LANG);
			setState(572);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class U_herkunftsland_kurzContext extends ParserRuleContext {
		public TerminalNode U_HERKUNFTSLAND_KURZ() { return getToken(LafParser.U_HERKUNFTSLAND_KURZ, 0); }
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
		enterRule(_localctx, 134, RULE_u_herkunftsland_kurz);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(574);
			match(U_HERKUNFTSLAND_KURZ);
			setState(575);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class U_herkunftsland_sContext extends ParserRuleContext {
		public TerminalNode U_HERKUNFTSLAND_S() { return getToken(LafParser.U_HERKUNFTSLAND_S, 0); }
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
		enterRule(_localctx, 136, RULE_u_herkunftsland_s);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(577);
			match(U_HERKUNFTSLAND_S);
			setState(578);
			match(STRING);
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
		enterRule(_localctx, 138, RULE_u_gemeindeschluessel);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(580);
			match(U_GEMEINDESCHLUESSEL);
			setState(581);
			match(STRING);
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
		enterRule(_localctx, 140, RULE_u_gemeindename);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(583);
			match(U_GEMEINDENAME);
			setState(584);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class U_orts_zusatzkennzahlContext extends ParserRuleContext {
		public TerminalNode U_ORTS_ZUSATZKENNZAHL() { return getToken(LafParser.U_ORTS_ZUSATZKENNZAHL, 0); }
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
		enterRule(_localctx, 142, RULE_u_orts_zusatzkennzahl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(586);
			match(U_ORTS_ZUSATZKENNZAHL);
			setState(587);
			match(STRING);
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
		enterRule(_localctx, 144, RULE_u_koordinaten);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(589);
			match(U_KOORDINATEN);
			setState(590);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(594);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(591);
				match(T__4);
				}
				}
				setState(596);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(597);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(601);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(598);
				match(T__4);
				}
				}
				setState(603);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(604);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class U_koordinaten_sContext extends ParserRuleContext {
		public TerminalNode U_KOORDINATEN_S() { return getToken(LafParser.U_KOORDINATEN_S, 0); }
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 146, RULE_u_koordinaten_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(606);
			match(U_KOORDINATEN_S);
			setState(607);
			match(STRING);
			setState(611);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(608);
				match(T__4);
				}
				}
				setState(613);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(614);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(618);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(615);
				match(T__4);
				}
				}
				setState(620);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(621);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class U_orts_zusatzcodeContext extends ParserRuleContext {
		public TerminalNode U_ORTS_ZUSATZCODE() { return getToken(LafParser.U_ORTS_ZUSATZCODE, 0); }
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
		enterRule(_localctx, 148, RULE_u_orts_zusatzcode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(623);
			match(U_ORTS_ZUSATZCODE);
			setState(624);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class U_orts_zusatztextContext extends ParserRuleContext {
		public TerminalNode U_ORTS_ZUSATZTEXT() { return getToken(LafParser.U_ORTS_ZUSATZTEXT, 0); }
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
		enterRule(_localctx, 150, RULE_u_orts_zusatztext);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(626);
			match(U_ORTS_ZUSATZTEXT);
			setState(627);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class U_nuts_codeContext extends ParserRuleContext {
		public TerminalNode U_NUTS_CODE() { return getToken(LafParser.U_NUTS_CODE, 0); }
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
		enterRule(_localctx, 152, RULE_u_nuts_code);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(629);
			match(U_NUTS_CODE);
			setState(630);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_herkunftsland_langContext extends ParserRuleContext {
		public TerminalNode P_HERKUNFTSLAND_LANG() { return getToken(LafParser.P_HERKUNFTSLAND_LANG, 0); }
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
		enterRule(_localctx, 154, RULE_p_herkunftsland_lang);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(632);
			match(P_HERKUNFTSLAND_LANG);
			setState(633);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_herkunftsland_kurzContext extends ParserRuleContext {
		public TerminalNode P_HERKUNFTSLAND_KURZ() { return getToken(LafParser.P_HERKUNFTSLAND_KURZ, 0); }
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
		enterRule(_localctx, 156, RULE_p_herkunftsland_kurz);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(635);
			match(P_HERKUNFTSLAND_KURZ);
			setState(636);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_herkunftsland_sContext extends ParserRuleContext {
		public TerminalNode P_HERKUNFTSLAND_S() { return getToken(LafParser.P_HERKUNFTSLAND_S, 0); }
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
		enterRule(_localctx, 158, RULE_p_herkunftsland_s);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(638);
			match(P_HERKUNFTSLAND_S);
			setState(639);
			match(STRING);
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
		enterRule(_localctx, 160, RULE_p_gemeindeschluessel);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(641);
			match(P_GEMEINDESCHLUESSEL);
			setState(642);
			match(STRING);
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
		enterRule(_localctx, 162, RULE_p_gemeindename);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(644);
			match(P_GEMEINDENAME);
			setState(645);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_orts_zusatzkennzahlContext extends ParserRuleContext {
		public TerminalNode P_ORTS_ZUSATZKENNZAHL() { return getToken(LafParser.P_ORTS_ZUSATZKENNZAHL, 0); }
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
		enterRule(_localctx, 164, RULE_p_orts_zusatzkennzahl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(647);
			match(P_ORTS_ZUSATZKENNZAHL);
			setState(648);
			match(STRING);
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
		enterRule(_localctx, 166, RULE_p_koordinaten);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(650);
			match(P_KOORDINATEN);
			setState(651);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(655);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(652);
				match(T__4);
				}
				}
				setState(657);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(658);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(662);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(659);
				match(T__4);
				}
				}
				setState(664);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(665);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_koordinaten_sContext extends ParserRuleContext {
		public TerminalNode P_KOORDINATEN_S() { return getToken(LafParser.P_KOORDINATEN_S, 0); }
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 168, RULE_p_koordinaten_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(667);
			match(P_KOORDINATEN_S);
			setState(668);
			match(STRING);
			setState(672);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(669);
				match(T__4);
				}
				}
				setState(674);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(675);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(679);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(676);
				match(T__4);
				}
				}
				setState(681);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(682);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_orts_zusatzcodeContext extends ParserRuleContext {
		public TerminalNode P_ORTS_ZUSATZCODE() { return getToken(LafParser.P_ORTS_ZUSATZCODE, 0); }
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
		enterRule(_localctx, 170, RULE_p_orts_zusatzcode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(684);
			match(P_ORTS_ZUSATZCODE);
			setState(685);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_orts_zusatztextContext extends ParserRuleContext {
		public TerminalNode P_ORTS_ZUSATZTEXT() { return getToken(LafParser.P_ORTS_ZUSATZTEXT, 0); }
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
		enterRule(_localctx, 172, RULE_p_orts_zusatztext);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(687);
			match(P_ORTS_ZUSATZTEXT);
			setState(688);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_nuts_codeContext extends ParserRuleContext {
		public TerminalNode P_NUTS_CODE() { return getToken(LafParser.P_NUTS_CODE, 0); }
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
		enterRule(_localctx, 174, RULE_p_nuts_code);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(690);
			match(P_NUTS_CODE);
			setState(691);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_site_idContext extends ParserRuleContext {
		public TerminalNode P_SITE_ID() { return getToken(LafParser.P_SITE_ID, 0); }
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
		enterRule(_localctx, 176, RULE_p_site_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(693);
			match(P_SITE_ID);
			setState(694);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_site_nameContext extends ParserRuleContext {
		public TerminalNode P_SITE_NAME() { return getToken(LafParser.P_SITE_NAME, 0); }
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
		enterRule(_localctx, 178, RULE_p_site_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(696);
			match(P_SITE_NAME);
			setState(697);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class P_hoehe_nnContext extends ParserRuleContext {
		public TerminalNode P_HOEHE_NN() { return getToken(LafParser.P_HOEHE_NN, 0); }
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
		enterRule(_localctx, 180, RULE_p_hoehe_nn);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(699);
			match(P_HOEHE_NN);
			setState(700);
			match(STRING);
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
		enterRule(_localctx, 182, RULE_p_hoehe_land);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(702);
			match(P_HOEHE_LAND);
			setState(703);
			match(STRING);
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
		enterRule(_localctx, 184, RULE_mehrzweckfeld);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(705);
			match(MEHRZWECKFELD);
			setState(706);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Mess_datum_uhrzeitContext extends ParserRuleContext {
		public TerminalNode MESS_DATUM_UHRZEIT() { return getToken(LafParser.MESS_DATUM_UHRZEIT, 0); }
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
		enterRule(_localctx, 186, RULE_mess_datum_uhrzeit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(708);
			match(MESS_DATUM_UHRZEIT);
			setState(709);
			match(STRING);
			setState(713);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(710);
				match(T__4);
				}
				}
				setState(715);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(716);
			match(STRING);
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
		enterRule(_localctx, 188, RULE_messzeit_sekunden);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(718);
			match(MESSZEIT_SEKUNDEN);
			setState(719);
			match(STRING);
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
		enterRule(_localctx, 190, RULE_messmethode_c);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(721);
			match(MESSMETHODE_C);
			setState(722);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Messmethode_sContext extends ParserRuleContext {
		public TerminalNode MESSMETHODE_S() { return getToken(LafParser.MESSMETHODE_S, 0); }
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
		enterRule(_localctx, 192, RULE_messmethode_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(724);
			match(MESSMETHODE_S);
			setState(725);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class BearbeitungsstatusContext extends ParserRuleContext {
		public TerminalNode BEARBEITUNGSSTATUS() { return getToken(LafParser.BEARBEITUNGSSTATUS, 0); }
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
		enterRule(_localctx, 194, RULE_bearbeitungsstatus);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(727);
			match(BEARBEITUNGSSTATUS);
			setState(728);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Pep_flagContext extends ParserRuleContext {
		public TerminalNode PEP_FLAG() { return getToken(LafParser.PEP_FLAG, 0); }
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
		enterRule(_localctx, 196, RULE_pep_flag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(730);
			match(PEP_FLAG);
			setState(731);
			match(STRING);
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
		enterRule(_localctx, 198, RULE_erfassung_abgeschlossen);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(733);
			match(ERFASSUNG_ABGESCHLOSSEN);
			setState(734);
			match(STRING);
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
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 200, RULE_probenzusatzbeschreibung);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(736);
			match(PROBENZUSATZBESCHREIBUNG);
			setState(737);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(741);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(738);
				match(T__4);
				}
				}
				setState(743);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(744);
			match(STRING);
			setState(748);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(745);
				match(T__4);
				}
				}
				setState(750);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(751);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(755);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(752);
				match(T__4);
				}
				}
				setState(757);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(758);
			match(STRING);
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
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public TerminalNode STRING_ESC() { return getToken(LafParser.STRING_ESC, 0); }
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
		enterRule(_localctx, 202, RULE_pzb_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(760);
			match(PZB_S);
			setState(761);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(765);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(762);
				match(T__4);
				}
				}
				setState(767);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(768);
			match(STRING);
			setState(772);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(769);
				match(T__4);
				}
				}
				setState(774);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(775);
			match(STRING);
			setState(779);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(776);
				match(T__4);
				}
				}
				setState(781);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(782);
			match(STRING);
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
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 204, RULE_messwert);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(784);
			match(MESSWERT);
			setState(785);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(789);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(786);
				match(T__4);
				}
				}
				setState(791);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(792);
			match(STRING);
			setState(796);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(793);
				match(T__4);
				}
				}
				setState(798);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(799);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(803);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(800);
				match(T__4);
				}
				}
				setState(805);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(807);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(806);
				match(STRING);
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

	public static class Messwert_sContext extends ParserRuleContext {
		public TerminalNode MESSWERT_S() { return getToken(LafParser.MESSWERT_S, 0); }
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
		enterRule(_localctx, 206, RULE_messwert_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(809);
			match(MESSWERT_S);
			setState(810);
			match(STRING);
			setState(814);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(811);
				match(T__4);
				}
				}
				setState(816);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(817);
			match(STRING);
			setState(821);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(818);
				match(T__4);
				}
				}
				setState(823);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(824);
			match(STRING);
			setState(828);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(825);
				match(T__4);
				}
				}
				setState(830);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(832);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(831);
				match(STRING);
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

	public static class Messwert_iContext extends ParserRuleContext {
		public TerminalNode MESSWERT_I() { return getToken(LafParser.MESSWERT_I, 0); }
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 208, RULE_messwert_i);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(834);
			match(MESSWERT_I);
			setState(835);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(839);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(836);
				match(T__4);
				}
				}
				setState(841);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(842);
			match(STRING);
			setState(846);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(843);
				match(T__4);
				}
				}
				setState(848);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(849);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(853);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(850);
				match(T__4);
				}
				}
				setState(855);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(863);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				setState(856);
				match(STRING);
				setState(860);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(857);
					match(T__4);
					}
					}
					setState(862);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(872);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				{
				setState(865);
				match(STRING);
				setState(869);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(866);
					match(T__4);
					}
					}
					setState(871);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(881);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				{
				setState(874);
				match(STRING);
				setState(878);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(875);
					match(T__4);
					}
					}
					setState(880);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(884);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(883);
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
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 210, RULE_messwert_g);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(886);
			match(MESSWERT_G);
			setState(887);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(891);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(888);
				match(T__4);
				}
				}
				setState(893);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(894);
			match(STRING);
			setState(898);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(895);
				match(T__4);
				}
				}
				setState(900);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(901);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(905);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(902);
				match(T__4);
				}
				}
				setState(907);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(915);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				{
				setState(908);
				match(STRING);
				setState(912);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(909);
					match(T__4);
					}
					}
					setState(914);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(924);
			switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
			case 1:
				{
				setState(917);
				match(STRING);
				setState(921);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(918);
					match(T__4);
					}
					}
					setState(923);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(933);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				{
				setState(926);
				match(STRING);
				setState(930);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(927);
					match(T__4);
					}
					}
					setState(932);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(936);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(935);
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
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 212, RULE_messwert_nwg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(938);
			match(MESSWERT_NWG);
			setState(939);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(943);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(940);
				match(T__4);
				}
				}
				setState(945);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(946);
			match(STRING);
			setState(950);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(947);
				match(T__4);
				}
				}
				setState(952);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(953);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(957);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(954);
				match(T__4);
				}
				}
				setState(959);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(967);
			switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
			case 1:
				{
				setState(960);
				match(STRING);
				setState(964);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(961);
					match(T__4);
					}
					}
					setState(966);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(970);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(969);
				match(STRING);
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

	public static class Messwert_nwg_sContext extends ParserRuleContext {
		public TerminalNode MESSWERT_NWG_S() { return getToken(LafParser.MESSWERT_NWG_S, 0); }
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
		enterRule(_localctx, 214, RULE_messwert_nwg_s);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(972);
			match(MESSWERT_NWG_S);
			setState(973);
			match(STRING);
			setState(977);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(974);
				match(T__4);
				}
				}
				setState(979);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(980);
			match(STRING);
			setState(984);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(981);
				match(T__4);
				}
				}
				setState(986);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(987);
			match(STRING);
			setState(991);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(988);
				match(T__4);
				}
				}
				setState(993);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1001);
			switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
			case 1:
				{
				setState(994);
				match(STRING);
				setState(998);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(995);
					match(T__4);
					}
					}
					setState(1000);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(1004);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(1003);
				match(STRING);
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

	public static class Messwert_nwg_iContext extends ParserRuleContext {
		public TerminalNode MESSWERT_NWG_I() { return getToken(LafParser.MESSWERT_NWG_I, 0); }
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 216, RULE_messwert_nwg_i);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1006);
			match(MESSWERT_NWG_I);
			setState(1007);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(1011);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1008);
				match(T__4);
				}
				}
				setState(1013);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1014);
			match(STRING);
			setState(1018);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1015);
				match(T__4);
				}
				}
				setState(1020);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1021);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(1025);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1022);
				match(T__4);
				}
				}
				setState(1027);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1035);
			switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
			case 1:
				{
				setState(1028);
				match(STRING);
				setState(1032);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(1029);
					match(T__4);
					}
					}
					setState(1034);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(1044);
			switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
			case 1:
				{
				setState(1037);
				match(STRING);
				setState(1041);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(1038);
					match(T__4);
					}
					}
					setState(1043);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(1053);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				{
				setState(1046);
				match(STRING);
				setState(1050);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(1047);
					match(T__4);
					}
					}
					setState(1052);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(1062);
			switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
			case 1:
				{
				setState(1055);
				match(STRING);
				setState(1059);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(1056);
					match(T__4);
					}
					}
					setState(1061);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(1064);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Messwert_nwg_gContext extends ParserRuleContext {
		public TerminalNode MESSWERT_NWG_G() { return getToken(LafParser.MESSWERT_NWG_G, 0); }
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 218, RULE_messwert_nwg_g);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1066);
			match(MESSWERT_NWG_G);
			setState(1067);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(1071);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1068);
				match(T__4);
				}
				}
				setState(1073);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1074);
			match(STRING);
			setState(1078);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1075);
				match(T__4);
				}
				}
				setState(1080);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1081);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(1085);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1082);
				match(T__4);
				}
				}
				setState(1087);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1095);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				{
				setState(1088);
				match(STRING);
				setState(1092);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(1089);
					match(T__4);
					}
					}
					setState(1094);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(1097);
			match(STRING);
			setState(1101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1098);
				match(T__4);
				}
				}
				setState(1103);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1111);
			switch ( getInterpreter().adaptivePredict(_input,107,_ctx) ) {
			case 1:
				{
				setState(1104);
				match(STRING);
				setState(1108);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(1105);
					match(T__4);
					}
					}
					setState(1110);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(1120);
			switch ( getInterpreter().adaptivePredict(_input,109,_ctx) ) {
			case 1:
				{
				setState(1113);
				match(STRING);
				setState(1117);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(1114);
					match(T__4);
					}
					}
					setState(1119);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(1123);
			_la = _input.LA(1);
			if (_la==STRING_ESC || _la==STRING) {
				{
				setState(1122);
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
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 220, RULE_kommentar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1125);
			match(KOMMENTAR);
			setState(1126);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(1130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1127);
				match(T__4);
				}
				}
				setState(1132);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1133);
			match(STRING);
			setState(1137);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1134);
				match(T__4);
				}
				}
				setState(1139);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1140);
			match(STRING);
			setState(1144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1141);
				match(T__4);
				}
				}
				setState(1146);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1147);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Kommentar_tContext extends ParserRuleContext {
		public TerminalNode KOMMENTAR_T() { return getToken(LafParser.KOMMENTAR_T, 0); }
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
		enterRule(_localctx, 222, RULE_kommentar_t);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1149);
			match(KOMMENTAR_T);
			setState(1150);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class ProbenkommentarContext extends ParserRuleContext {
		public TerminalNode PROBENKOMMENTAR() { return getToken(LafParser.PROBENKOMMENTAR, 0); }
		public List<TerminalNode> STRING() { return getTokens(LafParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(LafParser.STRING, i);
		}
		public List<TerminalNode> STRING_ESC() { return getTokens(LafParser.STRING_ESC); }
		public TerminalNode STRING_ESC(int i) {
			return getToken(LafParser.STRING_ESC, i);
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
		enterRule(_localctx, 224, RULE_probenkommentar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1152);
			match(PROBENKOMMENTAR);
			setState(1153);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(1157);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1154);
				match(T__4);
				}
				}
				setState(1159);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1160);
			match(STRING);
			setState(1164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1161);
				match(T__4);
				}
				}
				setState(1166);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1167);
			match(STRING);
			setState(1171);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1168);
				match(T__4);
				}
				}
				setState(1173);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1174);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static class Probenkommentar_tContext extends ParserRuleContext {
		public TerminalNode PROBENKOMMENTAR_T() { return getToken(LafParser.PROBENKOMMENTAR_T, 0); }
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
		enterRule(_localctx, 226, RULE_probenkommentar_t);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1176);
			match(PROBENKOMMENTAR_T);
			setState(1177);
			_la = _input.LA(1);
			if ( !(_la==STRING_ESC || _la==STRING) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3b\u049e\4\2\t\2\4"+
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
		"k\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\3\2\7\2\u00e8\n\2\f"+
		"\2\16\2\u00eb\13\2\3\2\5\2\u00ee\n\2\3\2\3\2\3\3\3\3\3\4\3\4\6\4\u00f6"+
		"\n\4\r\4\16\4\u00f7\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u0125\n\5\3\6"+
		"\5\6\u0128\n\6\3\6\6\6\u012b\n\6\r\6\16\6\u012c\3\7\3\7\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u013b\n\b\3\t\3\t\5\t\u013f\n\t\3\n\3\n"+
		"\5\n\u0143\n\n\3\13\3\13\5\13\u0147\n\13\3\f\3\f\5\f\u014b\n\f\3\r\3\r"+
		"\3\r\5\r\u0150\n\r\3\16\3\16\5\16\u0154\n\16\3\17\3\17\5\17\u0158\n\17"+
		"\3\20\3\20\5\20\u015c\n\20\3\21\3\21\5\21\u0160\n\21\3\22\3\22\5\22\u0164"+
		"\n\22\3\23\3\23\3\23\5\23\u0169\n\23\3\24\3\24\5\24\u016d\n\24\3\25\3"+
		"\25\5\25\u0171\n\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u017b"+
		"\n\26\3\27\3\27\5\27\u017f\n\27\3\30\3\30\5\30\u0183\n\30\3\31\3\31\7"+
		"\31\u0187\n\31\f\31\16\31\u018a\13\31\3\32\3\32\3\32\3\32\3\32\3\32\3"+
		"\32\5\32\u0193\n\32\3\33\3\33\3\33\5\33\u0198\n\33\3\34\3\34\5\34\u019c"+
		"\n\34\3\35\3\35\5\35\u01a0\n\35\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3"+
		" \3!\3!\3!\3\"\3\"\3\"\3#\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&\3&\3\'\3\'\3\'"+
		"\3(\3(\3(\3)\3)\3)\3*\3*\3*\3+\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\3.\3/\3/"+
		"\3/\3\60\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3"+
		"\64\3\64\3\65\3\65\3\65\7\65\u01ea\n\65\f\65\16\65\u01ed\13\65\3\65\3"+
		"\65\3\66\3\66\3\66\7\66\u01f4\n\66\f\66\16\66\u01f7\13\66\3\66\3\66\3"+
		"\67\3\67\3\67\7\67\u01fe\n\67\f\67\16\67\u0201\13\67\3\67\3\67\38\38\3"+
		"8\78\u0208\n8\f8\168\u020b\138\38\38\39\39\39\79\u0212\n9\f9\169\u0215"+
		"\139\39\39\3:\3:\3:\3;\3;\3;\3<\3<\3<\3=\3=\3=\3>\3>\3>\3?\3?\3?\7?\u022b"+
		"\n?\f?\16?\u022e\13?\3?\3?\3@\3@\3@\3A\3A\3A\3B\3B\3B\3C\3C\3C\3D\3D\3"+
		"D\3E\3E\3E\3F\3F\3F\3G\3G\3G\3H\3H\3H\3I\3I\3I\3J\3J\3J\7J\u0253\nJ\f"+
		"J\16J\u0256\13J\3J\3J\7J\u025a\nJ\fJ\16J\u025d\13J\3J\3J\3K\3K\3K\7K\u0264"+
		"\nK\fK\16K\u0267\13K\3K\3K\7K\u026b\nK\fK\16K\u026e\13K\3K\3K\3L\3L\3"+
		"L\3M\3M\3M\3N\3N\3N\3O\3O\3O\3P\3P\3P\3Q\3Q\3Q\3R\3R\3R\3S\3S\3S\3T\3"+
		"T\3T\3U\3U\3U\7U\u0290\nU\fU\16U\u0293\13U\3U\3U\7U\u0297\nU\fU\16U\u029a"+
		"\13U\3U\3U\3V\3V\3V\7V\u02a1\nV\fV\16V\u02a4\13V\3V\3V\7V\u02a8\nV\fV"+
		"\16V\u02ab\13V\3V\3V\3W\3W\3W\3X\3X\3X\3Y\3Y\3Y\3Z\3Z\3Z\3[\3[\3[\3\\"+
		"\3\\\3\\\3]\3]\3]\3^\3^\3^\3_\3_\3_\7_\u02ca\n_\f_\16_\u02cd\13_\3_\3"+
		"_\3`\3`\3`\3a\3a\3a\3b\3b\3b\3c\3c\3c\3d\3d\3d\3e\3e\3e\3f\3f\3f\7f\u02e6"+
		"\nf\ff\16f\u02e9\13f\3f\3f\7f\u02ed\nf\ff\16f\u02f0\13f\3f\3f\7f\u02f4"+
		"\nf\ff\16f\u02f7\13f\3f\3f\3g\3g\3g\7g\u02fe\ng\fg\16g\u0301\13g\3g\3"+
		"g\7g\u0305\ng\fg\16g\u0308\13g\3g\3g\7g\u030c\ng\fg\16g\u030f\13g\3g\3"+
		"g\3h\3h\3h\7h\u0316\nh\fh\16h\u0319\13h\3h\3h\7h\u031d\nh\fh\16h\u0320"+
		"\13h\3h\3h\7h\u0324\nh\fh\16h\u0327\13h\3h\5h\u032a\nh\3i\3i\3i\7i\u032f"+
		"\ni\fi\16i\u0332\13i\3i\3i\7i\u0336\ni\fi\16i\u0339\13i\3i\3i\7i\u033d"+
		"\ni\fi\16i\u0340\13i\3i\5i\u0343\ni\3j\3j\3j\7j\u0348\nj\fj\16j\u034b"+
		"\13j\3j\3j\7j\u034f\nj\fj\16j\u0352\13j\3j\3j\7j\u0356\nj\fj\16j\u0359"+
		"\13j\3j\3j\7j\u035d\nj\fj\16j\u0360\13j\5j\u0362\nj\3j\3j\7j\u0366\nj"+
		"\fj\16j\u0369\13j\5j\u036b\nj\3j\3j\7j\u036f\nj\fj\16j\u0372\13j\5j\u0374"+
		"\nj\3j\5j\u0377\nj\3k\3k\3k\7k\u037c\nk\fk\16k\u037f\13k\3k\3k\7k\u0383"+
		"\nk\fk\16k\u0386\13k\3k\3k\7k\u038a\nk\fk\16k\u038d\13k\3k\3k\7k\u0391"+
		"\nk\fk\16k\u0394\13k\5k\u0396\nk\3k\3k\7k\u039a\nk\fk\16k\u039d\13k\5"+
		"k\u039f\nk\3k\3k\7k\u03a3\nk\fk\16k\u03a6\13k\5k\u03a8\nk\3k\5k\u03ab"+
		"\nk\3l\3l\3l\7l\u03b0\nl\fl\16l\u03b3\13l\3l\3l\7l\u03b7\nl\fl\16l\u03ba"+
		"\13l\3l\3l\7l\u03be\nl\fl\16l\u03c1\13l\3l\3l\7l\u03c5\nl\fl\16l\u03c8"+
		"\13l\5l\u03ca\nl\3l\5l\u03cd\nl\3m\3m\3m\7m\u03d2\nm\fm\16m\u03d5\13m"+
		"\3m\3m\7m\u03d9\nm\fm\16m\u03dc\13m\3m\3m\7m\u03e0\nm\fm\16m\u03e3\13"+
		"m\3m\3m\7m\u03e7\nm\fm\16m\u03ea\13m\5m\u03ec\nm\3m\5m\u03ef\nm\3n\3n"+
		"\3n\7n\u03f4\nn\fn\16n\u03f7\13n\3n\3n\7n\u03fb\nn\fn\16n\u03fe\13n\3"+
		"n\3n\7n\u0402\nn\fn\16n\u0405\13n\3n\3n\7n\u0409\nn\fn\16n\u040c\13n\5"+
		"n\u040e\nn\3n\3n\7n\u0412\nn\fn\16n\u0415\13n\5n\u0417\nn\3n\3n\7n\u041b"+
		"\nn\fn\16n\u041e\13n\5n\u0420\nn\3n\3n\7n\u0424\nn\fn\16n\u0427\13n\5"+
		"n\u0429\nn\3n\3n\3o\3o\3o\7o\u0430\no\fo\16o\u0433\13o\3o\3o\7o\u0437"+
		"\no\fo\16o\u043a\13o\3o\3o\7o\u043e\no\fo\16o\u0441\13o\3o\3o\7o\u0445"+
		"\no\fo\16o\u0448\13o\5o\u044a\no\3o\3o\7o\u044e\no\fo\16o\u0451\13o\3"+
		"o\3o\7o\u0455\no\fo\16o\u0458\13o\5o\u045a\no\3o\3o\7o\u045e\no\fo\16"+
		"o\u0461\13o\5o\u0463\no\3o\5o\u0466\no\3p\3p\3p\7p\u046b\np\fp\16p\u046e"+
		"\13p\3p\3p\7p\u0472\np\fp\16p\u0475\13p\3p\3p\7p\u0479\np\fp\16p\u047c"+
		"\13p\3p\3p\3q\3q\3q\3r\3r\3r\7r\u0486\nr\fr\16r\u0489\13r\3r\3r\7r\u048d"+
		"\nr\fr\16r\u0490\13r\3r\3r\7r\u0494\nr\fr\16r\u0497\13r\3r\3r\3s\3s\3"+
		"s\3s\2\2t\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\66"+
		"8:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a"+
		"\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2"+
		"\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba"+
		"\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0\u00d2"+
		"\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\2\3\3\2`a\u04df"+
		"\2\u00e9\3\2\2\2\4\u00f1\3\2\2\2\6\u00f3\3\2\2\2\b\u0124\3\2\2\2\n\u0127"+
		"\3\2\2\2\f\u012e\3\2\2\2\16\u013a\3\2\2\2\20\u013e\3\2\2\2\22\u0142\3"+
		"\2\2\2\24\u0146\3\2\2\2\26\u014a\3\2\2\2\30\u014f\3\2\2\2\32\u0153\3\2"+
		"\2\2\34\u0157\3\2\2\2\36\u015b\3\2\2\2 \u015f\3\2\2\2\"\u0163\3\2\2\2"+
		"$\u0168\3\2\2\2&\u016c\3\2\2\2(\u0170\3\2\2\2*\u017a\3\2\2\2,\u017e\3"+
		"\2\2\2.\u0182\3\2\2\2\60\u0184\3\2\2\2\62\u0192\3\2\2\2\64\u0197\3\2\2"+
		"\2\66\u019b\3\2\2\28\u019f\3\2\2\2:\u01a1\3\2\2\2<\u01a4\3\2\2\2>\u01a7"+
		"\3\2\2\2@\u01aa\3\2\2\2B\u01ad\3\2\2\2D\u01b0\3\2\2\2F\u01b3\3\2\2\2H"+
		"\u01b6\3\2\2\2J\u01b9\3\2\2\2L\u01bc\3\2\2\2N\u01bf\3\2\2\2P\u01c2\3\2"+
		"\2\2R\u01c5\3\2\2\2T\u01c8\3\2\2\2V\u01cb\3\2\2\2X\u01ce\3\2\2\2Z\u01d1"+
		"\3\2\2\2\\\u01d4\3\2\2\2^\u01d7\3\2\2\2`\u01da\3\2\2\2b\u01dd\3\2\2\2"+
		"d\u01e0\3\2\2\2f\u01e3\3\2\2\2h\u01e6\3\2\2\2j\u01f0\3\2\2\2l\u01fa\3"+
		"\2\2\2n\u0204\3\2\2\2p\u020e\3\2\2\2r\u0218\3\2\2\2t\u021b\3\2\2\2v\u021e"+
		"\3\2\2\2x\u0221\3\2\2\2z\u0224\3\2\2\2|\u0227\3\2\2\2~\u0231\3\2\2\2\u0080"+
		"\u0234\3\2\2\2\u0082\u0237\3\2\2\2\u0084\u023a\3\2\2\2\u0086\u023d\3\2"+
		"\2\2\u0088\u0240\3\2\2\2\u008a\u0243\3\2\2\2\u008c\u0246\3\2\2\2\u008e"+
		"\u0249\3\2\2\2\u0090\u024c\3\2\2\2\u0092\u024f\3\2\2\2\u0094\u0260\3\2"+
		"\2\2\u0096\u0271\3\2\2\2\u0098\u0274\3\2\2\2\u009a\u0277\3\2\2\2\u009c"+
		"\u027a\3\2\2\2\u009e\u027d\3\2\2\2\u00a0\u0280\3\2\2\2\u00a2\u0283\3\2"+
		"\2\2\u00a4\u0286\3\2\2\2\u00a6\u0289\3\2\2\2\u00a8\u028c\3\2\2\2\u00aa"+
		"\u029d\3\2\2\2\u00ac\u02ae\3\2\2\2\u00ae\u02b1\3\2\2\2\u00b0\u02b4\3\2"+
		"\2\2\u00b2\u02b7\3\2\2\2\u00b4\u02ba\3\2\2\2\u00b6\u02bd\3\2\2\2\u00b8"+
		"\u02c0\3\2\2\2\u00ba\u02c3\3\2\2\2\u00bc\u02c6\3\2\2\2\u00be\u02d0\3\2"+
		"\2\2\u00c0\u02d3\3\2\2\2\u00c2\u02d6\3\2\2\2\u00c4\u02d9\3\2\2\2\u00c6"+
		"\u02dc\3\2\2\2\u00c8\u02df\3\2\2\2\u00ca\u02e2\3\2\2\2\u00cc\u02fa\3\2"+
		"\2\2\u00ce\u0312\3\2\2\2\u00d0\u032b\3\2\2\2\u00d2\u0344\3\2\2\2\u00d4"+
		"\u0378\3\2\2\2\u00d6\u03ac\3\2\2\2\u00d8\u03ce\3\2\2\2\u00da\u03f0\3\2"+
		"\2\2\u00dc\u042c\3\2\2\2\u00de\u0467\3\2\2\2\u00e0\u047f\3\2\2\2\u00e2"+
		"\u0482\3\2\2\2\u00e4\u049a\3\2\2\2\u00e6\u00e8\5\6\4\2\u00e7\u00e6\3\2"+
		"\2\2\u00e8\u00eb\3\2\2\2\u00e9\u00e7\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea"+
		"\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00ec\u00ee\5\4\3\2\u00ed\u00ec\3\2"+
		"\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f0\7\2\2\3\u00f0"+
		"\3\3\2\2\2\u00f1\u00f2\7\3\2\2\u00f2\5\3\2\2\2\u00f3\u00f5\7\4\2\2\u00f4"+
		"\u00f6\5\b\5\2\u00f5\u00f4\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f5\3\2"+
		"\2\2\u00f7\u00f8\3\2\2\2\u00f8\7\3\2\2\2\u00f9\u0125\5\20\t\2\u00fa\u0125"+
		"\5:\36\2\u00fb\u0125\5<\37\2\u00fc\u0125\5B\"\2\u00fd\u0125\5D#\2\u00fe"+
		"\u0125\5$\23\2\u00ff\u0125\5L\'\2\u0100\u0125\5N(\2\u0101\u0125\5P)\2"+
		"\u0102\u0125\5&\24\2\u0103\u0125\5\22\n\2\u0104\u0125\5^\60\2\u0105\u0125"+
		"\5`\61\2\u0106\u0125\5b\62\2\u0107\u0125\5\36\20\2\u0108\u0125\5n8\2\u0109"+
		"\u0125\5p9\2\u010a\u0125\5h\65\2\u010b\u0125\5j\66\2\u010c\u0125\5\24"+
		"\13\2\u010d\u0125\5v<\2\u010e\u0125\5\26\f\2\u010f\u0125\5|?\2\u0110\u0125"+
		"\5~@\2\u0111\u0125\5\u0080A\2\u0112\u0125\5\u0082B\2\u0113\u0125\5\u0084"+
		"C\2\u0114\u0125\5\60\31\2\u0115\u0125\5\30\r\2\u0116\u0125\5\32\16\2\u0117"+
		"\u0125\5\u00a6T\2\u0118\u0125\5\34\17\2\u0119\u0125\5\u00acW\2\u011a\u0125"+
		"\5\u00aeX\2\u011b\u0125\5\u00b0Y\2\u011c\u0125\5\u00b2Z\2\u011d\u0125"+
		"\5\u00b4[\2\u011e\u0125\5\u00b6\\\2\u011f\u0125\5\u00b8]\2\u0120\u0125"+
		"\5\u00ba^\2\u0121\u0125\5\n\6\2\u0122\u0125\5 \21\2\u0123\u0125\5\"\22"+
		"\2\u0124\u00f9\3\2\2\2\u0124\u00fa\3\2\2\2\u0124\u00fb\3\2\2\2\u0124\u00fc"+
		"\3\2\2\2\u0124\u00fd\3\2\2\2\u0124\u00fe\3\2\2\2\u0124\u00ff\3\2\2\2\u0124"+
		"\u0100\3\2\2\2\u0124\u0101\3\2\2\2\u0124\u0102\3\2\2\2\u0124\u0103\3\2"+
		"\2\2\u0124\u0104\3\2\2\2\u0124\u0105\3\2\2\2\u0124\u0106\3\2\2\2\u0124"+
		"\u0107\3\2\2\2\u0124\u0108\3\2\2\2\u0124\u0109\3\2\2\2\u0124\u010a\3\2"+
		"\2\2\u0124\u010b\3\2\2\2\u0124\u010c\3\2\2\2\u0124\u010d\3\2\2\2\u0124"+
		"\u010e\3\2\2\2\u0124\u010f\3\2\2\2\u0124\u0110\3\2\2\2\u0124\u0111\3\2"+
		"\2\2\u0124\u0112\3\2\2\2\u0124\u0113\3\2\2\2\u0124\u0114\3\2\2\2\u0124"+
		"\u0115\3\2\2\2\u0124\u0116\3\2\2\2\u0124\u0117\3\2\2\2\u0124\u0118\3\2"+
		"\2\2\u0124\u0119\3\2\2\2\u0124\u011a\3\2\2\2\u0124\u011b\3\2\2\2\u0124"+
		"\u011c\3\2\2\2\u0124\u011d\3\2\2\2\u0124\u011e\3\2\2\2\u0124\u011f\3\2"+
		"\2\2\u0124\u0120\3\2\2\2\u0124\u0121\3\2\2\2\u0124\u0122\3\2\2\2\u0124"+
		"\u0123\3\2\2\2\u0125\t\3\2\2\2\u0126\u0128\5\f\7\2\u0127\u0126\3\2\2\2"+
		"\u0127\u0128\3\2\2\2\u0128\u012a\3\2\2\2\u0129\u012b\5\16\b\2\u012a\u0129"+
		"\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d"+
		"\13\3\2\2\2\u012e\u012f\7\5\2\2\u012f\r\3\2\2\2\u0130\u013b\5R*\2\u0131"+
		"\u013b\5.\30\2\u0132\u013b\5\u00bc_\2\u0133\u013b\5\u00be`\2\u0134\u013b"+
		"\5(\25\2\u0135\u013b\5\u00c4c\2\u0136\u013b\5\u00c6d\2\u0137\u013b\5\u00c8"+
		"e\2\u0138\u013b\5*\26\2\u0139\u013b\5,\27\2\u013a\u0130\3\2\2\2\u013a"+
		"\u0131\3\2\2\2\u013a\u0132\3\2\2\2\u013a\u0133\3\2\2\2\u013a\u0134\3\2"+
		"\2\2\u013a\u0135\3\2\2\2\u013a\u0136\3\2\2\2\u013a\u0137\3\2\2\2\u013a"+
		"\u0138\3\2\2\2\u013a\u0139\3\2\2\2\u013b\17\3\2\2\2\u013c\u013f\5> \2"+
		"\u013d\u013f\5@!\2\u013e\u013c\3\2\2\2\u013e\u013d\3\2\2\2\u013f\21\3"+
		"\2\2\2\u0140\u0143\5Z.\2\u0141\u0143\5\\/\2\u0142\u0140\3\2\2\2\u0142"+
		"\u0141\3\2\2\2\u0143\23\3\2\2\2\u0144\u0147\5r:\2\u0145\u0147\5t;\2\u0146"+
		"\u0144\3\2\2\2\u0146\u0145\3\2\2\2\u0147\25\3\2\2\2\u0148\u014b\5x=\2"+
		"\u0149\u014b\5z>\2\u014a\u0148\3\2\2\2\u014a\u0149\3\2\2\2\u014b\27\3"+
		"\2\2\2\u014c\u0150\5\u009cO\2\u014d\u0150\5\u009eP\2\u014e\u0150\5\u00a0"+
		"Q\2\u014f\u014c\3\2\2\2\u014f\u014d\3\2\2\2\u014f\u014e\3\2\2\2\u0150"+
		"\31\3\2\2\2\u0151\u0154\5\u00a2R\2\u0152\u0154\5\u00a4S\2\u0153\u0151"+
		"\3\2\2\2\u0153\u0152\3\2\2\2\u0154\33\3\2\2\2\u0155\u0158\5\u00a8U\2\u0156"+
		"\u0158\5\u00aaV\2\u0157\u0155\3\2\2\2\u0157\u0156\3\2\2\2\u0158\35\3\2"+
		"\2\2\u0159\u015c\5d\63\2\u015a\u015c\5f\64\2\u015b\u0159\3\2\2\2\u015b"+
		"\u015a\3\2\2\2\u015c\37\3\2\2\2\u015d\u0160\5\u00caf\2\u015e\u0160\5\u00cc"+
		"g\2\u015f\u015d\3\2\2\2\u015f\u015e\3\2\2\2\u0160!\3\2\2\2\u0161\u0164"+
		"\5\u00e2r\2\u0162\u0164\5\u00e4s\2\u0163\u0161\3\2\2\2\u0163\u0162\3\2"+
		"\2\2\u0164#\3\2\2\2\u0165\u0169\5F$\2\u0166\u0169\5H%\2\u0167\u0169\5"+
		"J&\2\u0168\u0165\3\2\2\2\u0168\u0166\3\2\2\2\u0168\u0167\3\2\2\2\u0169"+
		"%\3\2\2\2\u016a\u016d\5T+\2\u016b\u016d\5V,\2\u016c\u016a\3\2\2\2\u016c"+
		"\u016b\3\2\2\2\u016d\'\3\2\2\2\u016e\u0171\5\u00c0a\2\u016f\u0171\5\u00c2"+
		"b\2\u0170\u016e\3\2\2\2\u0170\u016f\3\2\2\2\u0171)\3\2\2\2\u0172\u017b"+
		"\5\u00ceh\2\u0173\u017b\5\u00d0i\2\u0174\u017b\5\u00d2j\2\u0175\u017b"+
		"\5\u00d4k\2\u0176\u017b\5\u00d6l\2\u0177\u017b\5\u00d8m\2\u0178\u017b"+
		"\5\u00dan\2\u0179\u017b\5\u00dco\2\u017a\u0172\3\2\2\2\u017a\u0173\3\2"+
		"\2\2\u017a\u0174\3\2\2\2\u017a\u0175\3\2\2\2\u017a\u0176\3\2\2\2\u017a"+
		"\u0177\3\2\2\2\u017a\u0178\3\2\2\2\u017a\u0179\3\2\2\2\u017b+\3\2\2\2"+
		"\u017c\u017f\5\u00dep\2\u017d\u017f\5\u00e0q\2\u017e\u017c\3\2\2\2\u017e"+
		"\u017d\3\2\2\2\u017f-\3\2\2\2\u0180\u0183\5T+\2\u0181\u0183\5X-\2\u0182"+
		"\u0180\3\2\2\2\u0182\u0181\3\2\2\2\u0183/\3\2\2\2\u0184\u0188\7\6\2\2"+
		"\u0185\u0187\5\62\32\2\u0186\u0185\3\2\2\2\u0187\u018a\3\2\2\2\u0188\u0186"+
		"\3\2\2\2\u0188\u0189\3\2\2\2\u0189\61\3\2\2\2\u018a\u0188\3\2\2\2\u018b"+
		"\u0193\5\64\33\2\u018c\u0193\5\66\34\2\u018d\u0193\5\u0090I\2\u018e\u0193"+
		"\58\35\2\u018f\u0193\5\u0096L\2\u0190\u0193\5\u0098M\2\u0191\u0193\5\u009a"+
		"N\2\u0192\u018b\3\2\2\2\u0192\u018c\3\2\2\2\u0192\u018d\3\2\2\2\u0192"+
		"\u018e\3\2\2\2\u0192\u018f\3\2\2\2\u0192\u0190\3\2\2\2\u0192\u0191\3\2"+
		"\2\2\u0193\63\3\2\2\2\u0194\u0198\5\u0086D\2\u0195\u0198\5\u0088E\2\u0196"+
		"\u0198\5\u008aF\2\u0197\u0194\3\2\2\2\u0197\u0195\3\2\2\2\u0197\u0196"+
		"\3\2\2\2\u0198\65\3\2\2\2\u0199\u019c\5\u008cG\2\u019a\u019c\5\u008eH"+
		"\2\u019b\u0199\3\2\2\2\u019b\u019a\3\2\2\2\u019c\67\3\2\2\2\u019d\u01a0"+
		"\5\u0092J\2\u019e\u01a0\5\u0094K\2\u019f\u019d\3\2\2\2\u019f\u019e\3\2"+
		"\2\2\u01a09\3\2\2\2\u01a1\u01a2\7\b\2\2\u01a2\u01a3\t\2\2\2\u01a3;\3\2"+
		"\2\2\u01a4\u01a5\7\t\2\2\u01a5\u01a6\t\2\2\2\u01a6=\3\2\2\2\u01a7\u01a8"+
		"\7\n\2\2\u01a8\u01a9\t\2\2\2\u01a9?\3\2\2\2\u01aa\u01ab\7\13\2\2\u01ab"+
		"\u01ac\7a\2\2\u01acA\3\2\2\2\u01ad\u01ae\7\f\2\2\u01ae\u01af\t\2\2\2\u01af"+
		"C\3\2\2\2\u01b0\u01b1\7\r\2\2\u01b1\u01b2\t\2\2\2\u01b2E\3\2\2\2\u01b3"+
		"\u01b4\7\16\2\2\u01b4\u01b5\t\2\2\2\u01b5G\3\2\2\2\u01b6\u01b7\7\17\2"+
		"\2\u01b7\u01b8\t\2\2\2\u01b8I\3\2\2\2\u01b9\u01ba\7\20\2\2\u01ba\u01bb"+
		"\7a\2\2\u01bbK\3\2\2\2\u01bc\u01bd\7\21\2\2\u01bd\u01be\t\2\2\2\u01be"+
		"M\3\2\2\2\u01bf\u01c0\7\22\2\2\u01c0\u01c1\t\2\2\2\u01c1O\3\2\2\2\u01c2"+
		"\u01c3\7\23\2\2\u01c3\u01c4\t\2\2\2\u01c4Q\3\2\2\2\u01c5\u01c6\7\24\2"+
		"\2\u01c6\u01c7\7a\2\2\u01c7S\3\2\2\2\u01c8\u01c9\7\25\2\2\u01c9\u01ca"+
		"\t\2\2\2\u01caU\3\2\2\2\u01cb\u01cc\7\26\2\2\u01cc\u01cd\t\2\2\2\u01cd"+
		"W\3\2\2\2\u01ce\u01cf\7\27\2\2\u01cf\u01d0\t\2\2\2\u01d0Y\3\2\2\2\u01d1"+
		"\u01d2\7\30\2\2\u01d2\u01d3\t\2\2\2\u01d3[\3\2\2\2\u01d4\u01d5\7\31\2"+
		"\2\u01d5\u01d6\t\2\2\2\u01d6]\3\2\2\2\u01d7\u01d8\7\32\2\2\u01d8\u01d9"+
		"\t\2\2\2\u01d9_\3\2\2\2\u01da\u01db\7\33\2\2\u01db\u01dc\t\2\2\2\u01dc"+
		"a\3\2\2\2\u01dd\u01de\7\34\2\2\u01de\u01df\t\2\2\2\u01dfc\3\2\2\2\u01e0"+
		"\u01e1\7\35\2\2\u01e1\u01e2\t\2\2\2\u01e2e\3\2\2\2\u01e3\u01e4\7\36\2"+
		"\2\u01e4\u01e5\7a\2\2\u01e5g\3\2\2\2\u01e6\u01e7\7\37\2\2\u01e7\u01eb"+
		"\7a\2\2\u01e8\u01ea\7\7\2\2\u01e9\u01e8\3\2\2\2\u01ea\u01ed\3\2\2\2\u01eb"+
		"\u01e9\3\2\2\2\u01eb\u01ec\3\2\2\2\u01ec\u01ee\3\2\2\2\u01ed\u01eb\3\2"+
		"\2\2\u01ee\u01ef\7a\2\2\u01efi\3\2\2\2\u01f0\u01f1\7 \2\2\u01f1\u01f5"+
		"\7a\2\2\u01f2\u01f4\7\7\2\2\u01f3\u01f2\3\2\2\2\u01f4\u01f7\3\2\2\2\u01f5"+
		"\u01f3\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f8\3\2\2\2\u01f7\u01f5\3\2"+
		"\2\2\u01f8\u01f9\7a\2\2\u01f9k\3\2\2\2\u01fa\u01fb\7!\2\2\u01fb\u01ff"+
		"\7a\2\2\u01fc\u01fe\7\7\2\2\u01fd\u01fc\3\2\2\2\u01fe\u0201\3\2\2\2\u01ff"+
		"\u01fd\3\2\2\2\u01ff\u0200\3\2\2\2\u0200\u0202\3\2\2\2\u0201\u01ff\3\2"+
		"\2\2\u0202\u0203\7a\2\2\u0203m\3\2\2\2\u0204\u0205\7\"\2\2\u0205\u0209"+
		"\7a\2\2\u0206\u0208\7\7\2\2\u0207\u0206\3\2\2\2\u0208\u020b\3\2\2\2\u0209"+
		"\u0207\3\2\2\2\u0209\u020a\3\2\2\2\u020a\u020c\3\2\2\2\u020b\u0209\3\2"+
		"\2\2\u020c\u020d\7a\2\2\u020do\3\2\2\2\u020e\u020f\7#\2\2\u020f\u0213"+
		"\7a\2\2\u0210\u0212\7\7\2\2\u0211\u0210\3\2\2\2\u0212\u0215\3\2\2\2\u0213"+
		"\u0211\3\2\2\2\u0213\u0214\3\2\2\2\u0214\u0216\3\2\2\2\u0215\u0213\3\2"+
		"\2\2\u0216\u0217\7a\2\2\u0217q\3\2\2\2\u0218\u0219\7$\2\2\u0219\u021a"+
		"\t\2\2\2\u021as\3\2\2\2\u021b\u021c\7%\2\2\u021c\u021d\t\2\2\2\u021du"+
		"\3\2\2\2\u021e\u021f\7&\2\2\u021f\u0220\t\2\2\2\u0220w\3\2\2\2\u0221\u0222"+
		"\7\'\2\2\u0222\u0223\t\2\2\2\u0223y\3\2\2\2\u0224\u0225\7(\2\2\u0225\u0226"+
		"\t\2\2\2\u0226{\3\2\2\2\u0227\u0228\7)\2\2\u0228\u022c\7a\2\2\u0229\u022b"+
		"\7\7\2\2\u022a\u0229\3\2\2\2\u022b\u022e\3\2\2\2\u022c\u022a\3\2\2\2\u022c"+
		"\u022d\3\2\2\2\u022d\u022f\3\2\2\2\u022e\u022c\3\2\2\2\u022f\u0230\7a"+
		"\2\2\u0230}\3\2\2\2\u0231\u0232\7*\2\2\u0232\u0233\7a\2\2\u0233\177\3"+
		"\2\2\2\u0234\u0235\7+\2\2\u0235\u0236\t\2\2\2\u0236\u0081\3\2\2\2\u0237"+
		"\u0238\7,\2\2\u0238\u0239\t\2\2\2\u0239\u0083\3\2\2\2\u023a\u023b\7-\2"+
		"\2\u023b\u023c\7a\2\2\u023c\u0085\3\2\2\2\u023d\u023e\7.\2\2\u023e\u023f"+
		"\t\2\2\2\u023f\u0087\3\2\2\2\u0240\u0241\7/\2\2\u0241\u0242\t\2\2\2\u0242"+
		"\u0089\3\2\2\2\u0243\u0244\7\60\2\2\u0244\u0245\7a\2\2\u0245\u008b\3\2"+
		"\2\2\u0246\u0247\7\61\2\2\u0247\u0248\7a\2\2\u0248\u008d\3\2\2\2\u0249"+
		"\u024a\7\62\2\2\u024a\u024b\t\2\2\2\u024b\u008f\3\2\2\2\u024c\u024d\7"+
		"\63\2\2\u024d\u024e\7a\2\2\u024e\u0091\3\2\2\2\u024f\u0250\7\64\2\2\u0250"+
		"\u0254\t\2\2\2\u0251\u0253\7\7\2\2\u0252\u0251\3\2\2\2\u0253\u0256\3\2"+
		"\2\2\u0254\u0252\3\2\2\2\u0254\u0255\3\2\2\2\u0255\u0257\3\2\2\2\u0256"+
		"\u0254\3\2\2\2\u0257\u025b\t\2\2\2\u0258\u025a\7\7\2\2\u0259\u0258\3\2"+
		"\2\2\u025a\u025d\3\2\2\2\u025b\u0259\3\2\2\2\u025b\u025c\3\2\2\2\u025c"+
		"\u025e\3\2\2\2\u025d\u025b\3\2\2\2\u025e\u025f\t\2\2\2\u025f\u0093\3\2"+
		"\2\2\u0260\u0261\7\65\2\2\u0261\u0265\7a\2\2\u0262\u0264\7\7\2\2\u0263"+
		"\u0262\3\2\2\2\u0264\u0267\3\2\2\2\u0265\u0263\3\2\2\2\u0265\u0266\3\2"+
		"\2\2\u0266\u0268\3\2\2\2\u0267\u0265\3\2\2\2\u0268\u026c\t\2\2\2\u0269"+
		"\u026b\7\7\2\2\u026a\u0269\3\2\2\2\u026b\u026e\3\2\2\2\u026c\u026a\3\2"+
		"\2\2\u026c\u026d\3\2\2\2\u026d\u026f\3\2\2\2\u026e\u026c\3\2\2\2\u026f"+
		"\u0270\t\2\2\2\u0270\u0095\3\2\2\2\u0271\u0272\7\66\2\2\u0272\u0273\t"+
		"\2\2\2\u0273\u0097\3\2\2\2\u0274\u0275\7\67\2\2\u0275\u0276\t\2\2\2\u0276"+
		"\u0099\3\2\2\2\u0277\u0278\78\2\2\u0278\u0279\t\2\2\2\u0279\u009b\3\2"+
		"\2\2\u027a\u027b\79\2\2\u027b\u027c\t\2\2\2\u027c\u009d\3\2\2\2\u027d"+
		"\u027e\7:\2\2\u027e\u027f\t\2\2\2\u027f\u009f\3\2\2\2\u0280\u0281\7;\2"+
		"\2\u0281\u0282\7a\2\2\u0282\u00a1\3\2\2\2\u0283\u0284\7<\2\2\u0284\u0285"+
		"\7a\2\2\u0285\u00a3\3\2\2\2\u0286\u0287\7=\2\2\u0287\u0288\t\2\2\2\u0288"+
		"\u00a5\3\2\2\2\u0289\u028a\7>\2\2\u028a\u028b\7a\2\2\u028b\u00a7\3\2\2"+
		"\2\u028c\u028d\7?\2\2\u028d\u0291\t\2\2\2\u028e\u0290\7\7\2\2\u028f\u028e"+
		"\3\2\2\2\u0290\u0293\3\2\2\2\u0291\u028f\3\2\2\2\u0291\u0292\3\2\2\2\u0292"+
		"\u0294\3\2\2\2\u0293\u0291\3\2\2\2\u0294\u0298\t\2\2\2\u0295\u0297\7\7"+
		"\2\2\u0296\u0295\3\2\2\2\u0297\u029a\3\2\2\2\u0298\u0296\3\2\2\2\u0298"+
		"\u0299\3\2\2\2\u0299\u029b\3\2\2\2\u029a\u0298\3\2\2\2\u029b\u029c\t\2"+
		"\2\2\u029c\u00a9\3\2\2\2\u029d\u029e\7@\2\2\u029e\u02a2\7a\2\2\u029f\u02a1"+
		"\7\7\2\2\u02a0\u029f\3\2\2\2\u02a1\u02a4\3\2\2\2\u02a2\u02a0\3\2\2\2\u02a2"+
		"\u02a3\3\2\2\2\u02a3\u02a5\3\2\2\2\u02a4\u02a2\3\2\2\2\u02a5\u02a9\t\2"+
		"\2\2\u02a6\u02a8\7\7\2\2\u02a7\u02a6\3\2\2\2\u02a8\u02ab\3\2\2\2\u02a9"+
		"\u02a7\3\2\2\2\u02a9\u02aa\3\2\2\2\u02aa\u02ac\3\2\2\2\u02ab\u02a9\3\2"+
		"\2\2\u02ac\u02ad\t\2\2\2\u02ad\u00ab\3\2\2\2\u02ae\u02af\7A\2\2\u02af"+
		"\u02b0\t\2\2\2\u02b0\u00ad\3\2\2\2\u02b1\u02b2\7B\2\2\u02b2\u02b3\t\2"+
		"\2\2\u02b3\u00af\3\2\2\2\u02b4\u02b5\7C\2\2\u02b5\u02b6\t\2\2\2\u02b6"+
		"\u00b1\3\2\2\2\u02b7\u02b8\7D\2\2\u02b8\u02b9\t\2\2\2\u02b9\u00b3\3\2"+
		"\2\2\u02ba\u02bb\7E\2\2\u02bb\u02bc\t\2\2\2\u02bc\u00b5\3\2\2\2\u02bd"+
		"\u02be\7F\2\2\u02be\u02bf\7a\2\2\u02bf\u00b7\3\2\2\2\u02c0\u02c1\7G\2"+
		"\2\u02c1\u02c2\7a\2\2\u02c2\u00b9\3\2\2\2\u02c3\u02c4\7H\2\2\u02c4\u02c5"+
		"\t\2\2\2\u02c5\u00bb\3\2\2\2\u02c6\u02c7\7I\2\2\u02c7\u02cb\7a\2\2\u02c8"+
		"\u02ca\7\7\2\2\u02c9\u02c8\3\2\2\2\u02ca\u02cd\3\2\2\2\u02cb\u02c9\3\2"+
		"\2\2\u02cb\u02cc\3\2\2\2\u02cc\u02ce\3\2\2\2\u02cd\u02cb\3\2\2\2\u02ce"+
		"\u02cf\7a\2\2\u02cf\u00bd\3\2\2\2\u02d0\u02d1\7J\2\2\u02d1\u02d2\7a\2"+
		"\2\u02d2\u00bf\3\2\2\2\u02d3\u02d4\7K\2\2\u02d4\u02d5\t\2\2\2\u02d5\u00c1"+
		"\3\2\2\2\u02d6\u02d7\7L\2\2\u02d7\u02d8\t\2\2\2\u02d8\u00c3\3\2\2\2\u02d9"+
		"\u02da\7M\2\2\u02da\u02db\t\2\2\2\u02db\u00c5\3\2\2\2\u02dc\u02dd\7N\2"+
		"\2\u02dd\u02de\7a\2\2\u02de\u00c7\3\2\2\2\u02df\u02e0\7O\2\2\u02e0\u02e1"+
		"\7a\2\2\u02e1\u00c9\3\2\2\2\u02e2\u02e3\7P\2\2\u02e3\u02e7\t\2\2\2\u02e4"+
		"\u02e6\7\7\2\2\u02e5\u02e4\3\2\2\2\u02e6\u02e9\3\2\2\2\u02e7\u02e5\3\2"+
		"\2\2\u02e7\u02e8\3\2\2\2\u02e8\u02ea\3\2\2\2\u02e9\u02e7\3\2\2\2\u02ea"+
		"\u02ee\7a\2\2\u02eb\u02ed\7\7\2\2\u02ec\u02eb\3\2\2\2\u02ed\u02f0\3\2"+
		"\2\2\u02ee\u02ec\3\2\2\2\u02ee\u02ef\3\2\2\2\u02ef\u02f1\3\2\2\2\u02f0"+
		"\u02ee\3\2\2\2\u02f1\u02f5\t\2\2\2\u02f2\u02f4\7\7\2\2\u02f3\u02f2\3\2"+
		"\2\2\u02f4\u02f7\3\2\2\2\u02f5\u02f3\3\2\2\2\u02f5\u02f6\3\2\2\2\u02f6"+
		"\u02f8\3\2\2\2\u02f7\u02f5\3\2\2\2\u02f8\u02f9\7a\2\2\u02f9\u00cb\3\2"+
		"\2\2\u02fa\u02fb\7Q\2\2\u02fb\u02ff\t\2\2\2\u02fc\u02fe\7\7\2\2\u02fd"+
		"\u02fc\3\2\2\2\u02fe\u0301\3\2\2\2\u02ff\u02fd\3\2\2\2\u02ff\u0300\3\2"+
		"\2\2\u0300\u0302\3\2\2\2\u0301\u02ff\3\2\2\2\u0302\u0306\7a\2\2\u0303"+
		"\u0305\7\7\2\2\u0304\u0303\3\2\2\2\u0305\u0308\3\2\2\2\u0306\u0304\3\2"+
		"\2\2\u0306\u0307\3\2\2\2\u0307\u0309\3\2\2\2\u0308\u0306\3\2\2\2\u0309"+
		"\u030d\7a\2\2\u030a\u030c\7\7\2\2\u030b\u030a\3\2\2\2\u030c\u030f\3\2"+
		"\2\2\u030d\u030b\3\2\2\2\u030d\u030e\3\2\2\2\u030e\u0310\3\2\2\2\u030f"+
		"\u030d\3\2\2\2\u0310\u0311\7a\2\2\u0311\u00cd\3\2\2\2\u0312\u0313\7R\2"+
		"\2\u0313\u0317\t\2\2\2\u0314\u0316\7\7\2\2\u0315\u0314\3\2\2\2\u0316\u0319"+
		"\3\2\2\2\u0317\u0315\3\2\2\2\u0317\u0318\3\2\2\2\u0318\u031a\3\2\2\2\u0319"+
		"\u0317\3\2\2\2\u031a\u031e\7a\2\2\u031b\u031d\7\7\2\2\u031c\u031b\3\2"+
		"\2\2\u031d\u0320\3\2\2\2\u031e\u031c\3\2\2\2\u031e\u031f\3\2\2\2\u031f"+
		"\u0321\3\2\2\2\u0320\u031e\3\2\2\2\u0321\u0325\t\2\2\2\u0322\u0324\7\7"+
		"\2\2\u0323\u0322\3\2\2\2\u0324\u0327\3\2\2\2\u0325\u0323\3\2\2\2\u0325"+
		"\u0326\3\2\2\2\u0326\u0329\3\2\2\2\u0327\u0325\3\2\2\2\u0328\u032a\7a"+
		"\2\2\u0329\u0328\3\2\2\2\u0329\u032a\3\2\2\2\u032a\u00cf\3\2\2\2\u032b"+
		"\u032c\7S\2\2\u032c\u0330\7a\2\2\u032d\u032f\7\7\2\2\u032e\u032d\3\2\2"+
		"\2\u032f\u0332\3\2\2\2\u0330\u032e\3\2\2\2\u0330\u0331\3\2\2\2\u0331\u0333"+
		"\3\2\2\2\u0332\u0330\3\2\2\2\u0333\u0337\7a\2\2\u0334\u0336\7\7\2\2\u0335"+
		"\u0334\3\2\2\2\u0336\u0339\3\2\2\2\u0337\u0335\3\2\2\2\u0337\u0338\3\2"+
		"\2\2\u0338\u033a\3\2\2\2\u0339\u0337\3\2\2\2\u033a\u033e\7a\2\2\u033b"+
		"\u033d\7\7\2\2\u033c\u033b\3\2\2\2\u033d\u0340\3\2\2\2\u033e\u033c\3\2"+
		"\2\2\u033e\u033f\3\2\2\2\u033f\u0342\3\2\2\2\u0340\u033e\3\2\2\2\u0341"+
		"\u0343\7a\2\2\u0342\u0341\3\2\2\2\u0342\u0343\3\2\2\2\u0343\u00d1\3\2"+
		"\2\2\u0344\u0345\7T\2\2\u0345\u0349\t\2\2\2\u0346\u0348\7\7\2\2\u0347"+
		"\u0346\3\2\2\2\u0348\u034b\3\2\2\2\u0349\u0347\3\2\2\2\u0349\u034a\3\2"+
		"\2\2\u034a\u034c\3\2\2\2\u034b\u0349\3\2\2\2\u034c\u0350\7a\2\2\u034d"+
		"\u034f\7\7\2\2\u034e\u034d\3\2\2\2\u034f\u0352\3\2\2\2\u0350\u034e\3\2"+
		"\2\2\u0350\u0351\3\2\2\2\u0351\u0353\3\2\2\2\u0352\u0350\3\2\2\2\u0353"+
		"\u0357\t\2\2\2\u0354\u0356\7\7\2\2\u0355\u0354\3\2\2\2\u0356\u0359\3\2"+
		"\2\2\u0357\u0355\3\2\2\2\u0357\u0358\3\2\2\2\u0358\u0361\3\2\2\2\u0359"+
		"\u0357\3\2\2\2\u035a\u035e\7a\2\2\u035b\u035d\7\7\2\2\u035c\u035b\3\2"+
		"\2\2\u035d\u0360\3\2\2\2\u035e\u035c\3\2\2\2\u035e\u035f\3\2\2\2\u035f"+
		"\u0362\3\2\2\2\u0360\u035e\3\2\2\2\u0361\u035a\3\2\2\2\u0361\u0362\3\2"+
		"\2\2\u0362\u036a\3\2\2\2\u0363\u0367\7a\2\2\u0364\u0366\7\7\2\2\u0365"+
		"\u0364\3\2\2\2\u0366\u0369\3\2\2\2\u0367\u0365\3\2\2\2\u0367\u0368\3\2"+
		"\2\2\u0368\u036b\3\2\2\2\u0369\u0367\3\2\2\2\u036a\u0363\3\2\2\2\u036a"+
		"\u036b\3\2\2\2\u036b\u0373\3\2\2\2\u036c\u0370\7a\2\2\u036d\u036f\7\7"+
		"\2\2\u036e\u036d\3\2\2\2\u036f\u0372\3\2\2\2\u0370\u036e\3\2\2\2\u0370"+
		"\u0371\3\2\2\2\u0371\u0374\3\2\2\2\u0372\u0370\3\2\2\2\u0373\u036c\3\2"+
		"\2\2\u0373\u0374\3\2\2\2\u0374\u0376\3\2\2\2\u0375\u0377\t\2\2\2\u0376"+
		"\u0375\3\2\2\2\u0376\u0377\3\2\2\2\u0377\u00d3\3\2\2\2\u0378\u0379\7U"+
		"\2\2\u0379\u037d\t\2\2\2\u037a\u037c\7\7\2\2\u037b\u037a\3\2\2\2\u037c"+
		"\u037f\3\2\2\2\u037d\u037b\3\2\2\2\u037d\u037e\3\2\2\2\u037e\u0380\3\2"+
		"\2\2\u037f\u037d\3\2\2\2\u0380\u0384\7a\2\2\u0381\u0383\7\7\2\2\u0382"+
		"\u0381\3\2\2\2\u0383\u0386\3\2\2\2\u0384\u0382\3\2\2\2\u0384\u0385\3\2"+
		"\2\2\u0385\u0387\3\2\2\2\u0386\u0384\3\2\2\2\u0387\u038b\t\2\2\2\u0388"+
		"\u038a\7\7\2\2\u0389\u0388\3\2\2\2\u038a\u038d\3\2\2\2\u038b\u0389\3\2"+
		"\2\2\u038b\u038c\3\2\2\2\u038c\u0395\3\2\2\2\u038d\u038b\3\2\2\2\u038e"+
		"\u0392\7a\2\2\u038f\u0391\7\7\2\2\u0390\u038f\3\2\2\2\u0391\u0394\3\2"+
		"\2\2\u0392\u0390\3\2\2\2\u0392\u0393\3\2\2\2\u0393\u0396\3\2\2\2\u0394"+
		"\u0392\3\2\2\2\u0395\u038e\3\2\2\2\u0395\u0396\3\2\2\2\u0396\u039e\3\2"+
		"\2\2\u0397\u039b\7a\2\2\u0398\u039a\7\7\2\2\u0399\u0398\3\2\2\2\u039a"+
		"\u039d\3\2\2\2\u039b\u0399\3\2\2\2\u039b\u039c\3\2\2\2\u039c\u039f\3\2"+
		"\2\2\u039d\u039b\3\2\2\2\u039e\u0397\3\2\2\2\u039e\u039f\3\2\2\2\u039f"+
		"\u03a7\3\2\2\2\u03a0\u03a4\7a\2\2\u03a1\u03a3\7\7\2\2\u03a2\u03a1\3\2"+
		"\2\2\u03a3\u03a6\3\2\2\2\u03a4\u03a2\3\2\2\2\u03a4\u03a5\3\2\2\2\u03a5"+
		"\u03a8\3\2\2\2\u03a6\u03a4\3\2\2\2\u03a7\u03a0\3\2\2\2\u03a7\u03a8\3\2"+
		"\2\2\u03a8\u03aa\3\2\2\2\u03a9\u03ab\t\2\2\2\u03aa\u03a9\3\2\2\2\u03aa"+
		"\u03ab\3\2\2\2\u03ab\u00d5\3\2\2\2\u03ac\u03ad\7V\2\2\u03ad\u03b1\t\2"+
		"\2\2\u03ae\u03b0\7\7\2\2\u03af\u03ae\3\2\2\2\u03b0\u03b3\3\2\2\2\u03b1"+
		"\u03af\3\2\2\2\u03b1\u03b2\3\2\2\2\u03b2\u03b4\3\2\2\2\u03b3\u03b1\3\2"+
		"\2\2\u03b4\u03b8\7a\2\2\u03b5\u03b7\7\7\2\2\u03b6\u03b5\3\2\2\2\u03b7"+
		"\u03ba\3\2\2\2\u03b8\u03b6\3\2\2\2\u03b8\u03b9\3\2\2\2\u03b9\u03bb\3\2"+
		"\2\2\u03ba\u03b8\3\2\2\2\u03bb\u03bf\t\2\2\2\u03bc\u03be\7\7\2\2\u03bd"+
		"\u03bc\3\2\2\2\u03be\u03c1\3\2\2\2\u03bf\u03bd\3\2\2\2\u03bf\u03c0\3\2"+
		"\2\2\u03c0\u03c9\3\2\2\2\u03c1\u03bf\3\2\2\2\u03c2\u03c6\7a\2\2\u03c3"+
		"\u03c5\7\7\2\2\u03c4\u03c3\3\2\2\2\u03c5\u03c8\3\2\2\2\u03c6\u03c4\3\2"+
		"\2\2\u03c6\u03c7\3\2\2\2\u03c7\u03ca\3\2\2\2\u03c8\u03c6\3\2\2\2\u03c9"+
		"\u03c2\3\2\2\2\u03c9\u03ca\3\2\2\2\u03ca\u03cc\3\2\2\2\u03cb\u03cd\7a"+
		"\2\2\u03cc\u03cb\3\2\2\2\u03cc\u03cd\3\2\2\2\u03cd\u00d7\3\2\2\2\u03ce"+
		"\u03cf\7W\2\2\u03cf\u03d3\7a\2\2\u03d0\u03d2\7\7\2\2\u03d1\u03d0\3\2\2"+
		"\2\u03d2\u03d5\3\2\2\2\u03d3\u03d1\3\2\2\2\u03d3\u03d4\3\2\2\2\u03d4\u03d6"+
		"\3\2\2\2\u03d5\u03d3\3\2\2\2\u03d6\u03da\7a\2\2\u03d7\u03d9\7\7\2\2\u03d8"+
		"\u03d7\3\2\2\2\u03d9\u03dc\3\2\2\2\u03da\u03d8\3\2\2\2\u03da\u03db\3\2"+
		"\2\2\u03db\u03dd\3\2\2\2\u03dc\u03da\3\2\2\2\u03dd\u03e1\7a\2\2\u03de"+
		"\u03e0\7\7\2\2\u03df\u03de\3\2\2\2\u03e0\u03e3\3\2\2\2\u03e1\u03df\3\2"+
		"\2\2\u03e1\u03e2\3\2\2\2\u03e2\u03eb\3\2\2\2\u03e3\u03e1\3\2\2\2\u03e4"+
		"\u03e8\7a\2\2\u03e5\u03e7\7\7\2\2\u03e6\u03e5\3\2\2\2\u03e7\u03ea\3\2"+
		"\2\2\u03e8\u03e6\3\2\2\2\u03e8\u03e9\3\2\2\2\u03e9\u03ec\3\2\2\2\u03ea"+
		"\u03e8\3\2\2\2\u03eb\u03e4\3\2\2\2\u03eb\u03ec\3\2\2\2\u03ec\u03ee\3\2"+
		"\2\2\u03ed\u03ef\7a\2\2\u03ee\u03ed\3\2\2\2\u03ee\u03ef\3\2\2\2\u03ef"+
		"\u00d9\3\2\2\2\u03f0\u03f1\7X\2\2\u03f1\u03f5\t\2\2\2\u03f2\u03f4\7\7"+
		"\2\2\u03f3\u03f2\3\2\2\2\u03f4\u03f7\3\2\2\2\u03f5\u03f3\3\2\2\2\u03f5"+
		"\u03f6\3\2\2\2\u03f6\u03f8\3\2\2\2\u03f7\u03f5\3\2\2\2\u03f8\u03fc\7a"+
		"\2\2\u03f9\u03fb\7\7\2\2\u03fa\u03f9\3\2\2\2\u03fb\u03fe\3\2\2\2\u03fc"+
		"\u03fa\3\2\2\2\u03fc\u03fd\3\2\2\2\u03fd\u03ff\3\2\2\2\u03fe\u03fc\3\2"+
		"\2\2\u03ff\u0403\t\2\2\2\u0400\u0402\7\7\2\2\u0401\u0400\3\2\2\2\u0402"+
		"\u0405\3\2\2\2\u0403\u0401\3\2\2\2\u0403\u0404\3\2\2\2\u0404\u040d\3\2"+
		"\2\2\u0405\u0403\3\2\2\2\u0406\u040a\7a\2\2\u0407\u0409\7\7\2\2\u0408"+
		"\u0407\3\2\2\2\u0409\u040c\3\2\2\2\u040a\u0408\3\2\2\2\u040a\u040b\3\2"+
		"\2\2\u040b\u040e\3\2\2\2\u040c\u040a\3\2\2\2\u040d\u0406\3\2\2\2\u040d"+
		"\u040e\3\2\2\2\u040e\u0416\3\2\2\2\u040f\u0413\7a\2\2\u0410\u0412\7\7"+
		"\2\2\u0411\u0410\3\2\2\2\u0412\u0415\3\2\2\2\u0413\u0411\3\2\2\2\u0413"+
		"\u0414\3\2\2\2\u0414\u0417\3\2\2\2\u0415\u0413\3\2\2\2\u0416\u040f\3\2"+
		"\2\2\u0416\u0417\3\2\2\2\u0417\u041f\3\2\2\2\u0418\u041c\7a\2\2\u0419"+
		"\u041b\7\7\2\2\u041a\u0419\3\2\2\2\u041b\u041e\3\2\2\2\u041c\u041a\3\2"+
		"\2\2\u041c\u041d\3\2\2\2\u041d\u0420\3\2\2\2\u041e\u041c\3\2\2\2\u041f"+
		"\u0418\3\2\2\2\u041f\u0420\3\2\2\2\u0420\u0428\3\2\2\2\u0421\u0425\7a"+
		"\2\2\u0422\u0424\7\7\2\2\u0423\u0422\3\2\2\2\u0424\u0427\3\2\2\2\u0425"+
		"\u0423\3\2\2\2\u0425\u0426\3\2\2\2\u0426\u0429\3\2\2\2\u0427\u0425\3\2"+
		"\2\2\u0428\u0421\3\2\2\2\u0428\u0429\3\2\2\2\u0429\u042a\3\2\2\2\u042a"+
		"\u042b\t\2\2\2\u042b\u00db\3\2\2\2\u042c\u042d\7Y\2\2\u042d\u0431\t\2"+
		"\2\2\u042e\u0430\7\7\2\2\u042f\u042e\3\2\2\2\u0430\u0433\3\2\2\2\u0431"+
		"\u042f\3\2\2\2\u0431\u0432\3\2\2\2\u0432\u0434\3\2\2\2\u0433\u0431\3\2"+
		"\2\2\u0434\u0438\7a\2\2\u0435\u0437\7\7\2\2\u0436\u0435\3\2\2\2\u0437"+
		"\u043a\3\2\2\2\u0438\u0436\3\2\2\2\u0438\u0439\3\2\2\2\u0439\u043b\3\2"+
		"\2\2\u043a\u0438\3\2\2\2\u043b\u043f\t\2\2\2\u043c\u043e\7\7\2\2\u043d"+
		"\u043c\3\2\2\2\u043e\u0441\3\2\2\2\u043f\u043d\3\2\2\2\u043f\u0440\3\2"+
		"\2\2\u0440\u0449\3\2\2\2\u0441\u043f\3\2\2\2\u0442\u0446\7a\2\2\u0443"+
		"\u0445\7\7\2\2\u0444\u0443\3\2\2\2\u0445\u0448\3\2\2\2\u0446\u0444\3\2"+
		"\2\2\u0446\u0447\3\2\2\2\u0447\u044a\3\2\2\2\u0448\u0446\3\2\2\2\u0449"+
		"\u0442\3\2\2\2\u0449\u044a\3\2\2\2\u044a\u044b\3\2\2\2\u044b\u044f\7a"+
		"\2\2\u044c\u044e\7\7\2\2\u044d\u044c\3\2\2\2\u044e\u0451\3\2\2\2\u044f"+
		"\u044d\3\2\2\2\u044f\u0450\3\2\2\2\u0450\u0459\3\2\2\2\u0451\u044f\3\2"+
		"\2\2\u0452\u0456\7a\2\2\u0453\u0455\7\7\2\2\u0454\u0453\3\2\2\2\u0455"+
		"\u0458\3\2\2\2\u0456\u0454\3\2\2\2\u0456\u0457\3\2\2\2\u0457\u045a\3\2"+
		"\2\2\u0458\u0456\3\2\2\2\u0459\u0452\3\2\2\2\u0459\u045a\3\2\2\2\u045a"+
		"\u0462\3\2\2\2\u045b\u045f\7a\2\2\u045c\u045e\7\7\2\2\u045d\u045c\3\2"+
		"\2\2\u045e\u0461\3\2\2\2\u045f\u045d\3\2\2\2\u045f\u0460\3\2\2\2\u0460"+
		"\u0463\3\2\2\2\u0461\u045f\3\2\2\2\u0462\u045b\3\2\2\2\u0462\u0463\3\2"+
		"\2\2\u0463\u0465\3\2\2\2\u0464\u0466\t\2\2\2\u0465\u0464\3\2\2\2\u0465"+
		"\u0466\3\2\2\2\u0466\u00dd\3\2\2\2\u0467\u0468\7Z\2\2\u0468\u046c\t\2"+
		"\2\2\u0469\u046b\7\7\2\2\u046a\u0469\3\2\2\2\u046b\u046e\3\2\2\2\u046c"+
		"\u046a\3\2\2\2\u046c\u046d\3\2\2\2\u046d\u046f\3\2\2\2\u046e\u046c\3\2"+
		"\2\2\u046f\u0473\7a\2\2\u0470\u0472\7\7\2\2\u0471\u0470\3\2\2\2\u0472"+
		"\u0475\3\2\2\2\u0473\u0471\3\2\2\2\u0473\u0474\3\2\2\2\u0474\u0476\3\2"+
		"\2\2\u0475\u0473\3\2\2\2\u0476\u047a\7a\2\2\u0477\u0479\7\7\2\2\u0478"+
		"\u0477\3\2\2\2\u0479\u047c\3\2\2\2\u047a\u0478\3\2\2\2\u047a\u047b\3\2"+
		"\2\2\u047b\u047d\3\2\2\2\u047c\u047a\3\2\2\2\u047d\u047e\t\2\2\2\u047e"+
		"\u00df\3\2\2\2\u047f\u0480\7[\2\2\u0480\u0481\t\2\2\2\u0481\u00e1\3\2"+
		"\2\2\u0482\u0483\7\\\2\2\u0483\u0487\t\2\2\2\u0484\u0486\7\7\2\2\u0485"+
		"\u0484\3\2\2\2\u0486\u0489\3\2\2\2\u0487\u0485\3\2\2\2\u0487\u0488\3\2"+
		"\2\2\u0488\u048a\3\2\2\2\u0489\u0487\3\2\2\2\u048a\u048e\7a\2\2\u048b"+
		"\u048d\7\7\2\2\u048c\u048b\3\2\2\2\u048d\u0490\3\2\2\2\u048e\u048c\3\2"+
		"\2\2\u048e\u048f\3\2\2\2\u048f\u0491\3\2\2\2\u0490\u048e\3\2\2\2\u0491"+
		"\u0495\7a\2\2\u0492\u0494\7\7\2\2\u0493\u0492\3\2\2\2\u0494\u0497\3\2"+
		"\2\2\u0495\u0493\3\2\2\2\u0495\u0496\3\2\2\2\u0496\u0498\3\2\2\2\u0497"+
		"\u0495\3\2\2\2\u0498\u0499\t\2\2\2\u0499\u00e3\3\2\2\2\u049a\u049b\7]"+
		"\2\2\u049b\u049c\t\2\2\2\u049c\u00e5\3\2\2\2w\u00e9\u00ed\u00f7\u0124"+
		"\u0127\u012c\u013a\u013e\u0142\u0146\u014a\u014f\u0153\u0157\u015b\u015f"+
		"\u0163\u0168\u016c\u0170\u017a\u017e\u0182\u0188\u0192\u0197\u019b\u019f"+
		"\u01eb\u01f5\u01ff\u0209\u0213\u022c\u0254\u025b\u0265\u026c\u0291\u0298"+
		"\u02a2\u02a9\u02cb\u02e7\u02ee\u02f5\u02ff\u0306\u030d\u0317\u031e\u0325"+
		"\u0329\u0330\u0337\u033e\u0342\u0349\u0350\u0357\u035e\u0361\u0367\u036a"+
		"\u0370\u0373\u0376\u037d\u0384\u038b\u0392\u0395\u039b\u039e\u03a4\u03a7"+
		"\u03aa\u03b1\u03b8\u03bf\u03c6\u03c9\u03cc\u03d3\u03da\u03e1\u03e8\u03eb"+
		"\u03ee\u03f5\u03fc\u0403\u040a\u040d\u0413\u0416\u041c\u041f\u0425\u0428"+
		"\u0431\u0438\u043f\u0446\u0449\u044f\u0456\u0459\u045f\u0462\u0465\u046c"+
		"\u0473\u047a\u0487\u048e\u0495";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}