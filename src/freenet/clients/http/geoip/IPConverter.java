package freenet.clients.http.geoip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import freenet.node.Node;
import freenet.support.Logger;

public class IPConverter {
	// Regex indicating ipranges start
	private static final String START = "##start##";
	final int MAX_ENTRIES = 100;
	// Local cache
	@SuppressWarnings("serial")
	private final HashMap<Long, Country> cache = new LinkedHashMap<Long, Country>() {
		@Override
		protected boolean removeEldestEntry(Map.Entry<Long, Country> eldest) {
			return size() > MAX_ENTRIES;
		}
	};
	// Cached DB file content
	private WeakReference<Cache> fullCache;
	// Reference to singleton object
	private static IPConverter instance;
	// File containing IP ranges
	private File dbFile;

	public enum Country {
		L0("localhost"), I0("IntraNet"), A1("Anonymous Proxy"), A2(
				"Satellite Provider"), AP("AP Asia/Pacific Region"), AF(
				"AFGHANISTAN"), AX("ALAND ISLANDS"), AL("ALBANIA"), AN(
				"NETHERLANDS ANTILLES"), DZ("ALGERIA"), AS("AMERICAN SAMOA"), AD(
				"ANDORRA"), AO("ANGOLA"), AI("ANGUILLA"), AQ("ANTARCTICA"), AG(
				"ANTIGUA AND BARBUDA"), AR("ARGENTINA"), AM("ARMENIA"), AW(
				"ARUBA"), AU("AUSTRALIA"), AT("AUSTRIA"), AZ("AZERBAIJAN"), BS(
				"BAHAMAS"), BH("BAHRAIN "), BD("BANGLADESH "), BB("BARBADOS "), BY(
				"BELARUS "), BE("BELGIUM "), BZ("BELIZE "), BJ("BENIN "), BM(
				"BERMUDA "), BT("BHUTAN "), BO(
				"BOLIVIA, PLURINATIONAL STATE OF "), BQ(
				"BONAIRE, SAINT EUSTATIUS AND SABA "), BA(
				"BOSNIA AND HERZEGOVINA "), BW("BOTSWANA "), BV(
				"BOUVET ISLAND "), BR("BRAZIL "), IO(
				"BRITISH INDIAN OCEAN TERRITORY "), BN("BRUNEI DARUSSALAM "), BG(
				"BULGARIA "), BF("BURKINA FASO "), BI("BURUNDI "), KH(
				"CAMBODIA "), CM("CAMEROON "), CA("CANADA "), CV("CAPE VERDE "), KY(
				"CAYMAN ISLANDS "), CF("CENTRAL AFRICAN REPUBLIC "), TD("CHAD "), CL(
				"CHILE "), CN("CHINA "), CX("CHRISTMAS ISLAND "), CC(
				"COCOS (KEELING) ISLANDS "), CO("COLOMBIA "), KM("COMOROS "), CG(
				"CONGO "), CD("CONGO, THE DEMOCRATIC REPUBLIC OF THE "), CK(
				"COOK ISLANDS "), CR("COSTA RICA "), CI("COTE D'IVOIRE "), HR(
				"CROATIA "), CU("CUBA "), CW("CURACAO "), CY("CYPRUS "), CZ(
				"CZECH REPUBLIC "), DK("DENMARK "), DJ("DJIBOUTI "), DM(
				"DOMINICA "), DO("DOMINICAN REPUBLIC "), EC("ECUADOR "), EG(
				"EGYPT "), SV("EL SALVADOR "), GQ("EQUATORIAL GUINEA "), ER(
				"ERITREA "), EE("ESTONIA "), ET("ETHIOPIA "), FK(
				"FALKLAND ISLANDS (MALVINAS) "), FO("FAROE ISLANDS "), FJ(
				"FIJI "), FI("FINLAND "), FR("FRANCE "), GF("FRENCH GUIANA "), PF(
				"FRENCH POLYNESIA "), TF("FRENCH SOUTHERN TERRITORIES "), GA(
				"GABON "), GM("GAMBIA "), GE("GEORGIA "), DE("GERMANY "), GH(
				"GHANA "), GI("GIBRALTAR "), GR("GREECE "), GL("GREENLAND "), GD(
				"GRENADA "), GP("GUADELOUPE "), GU("GUAM "), GT("GUATEMALA "), GG(
				"GUERNSEY "), GN("GUINEA "), GW("GUINEA-BISSAU "), GY("GUYANA "), HT(
				"HAITI "), HM("HEARD ISLAND AND MCDONALD ISLANDS "), VA(
				"HOLY SEE (VATICAN CITY STATE) "), HN("HONDURAS "), HK(
				"HONG KONG "), HU("HUNGARY "), IS("ICELAND "), IN("INDIA "), ID(
				"INDONESIA "), IR("IRAN, ISLAMIC REPUBLIC OF "), IQ("IRAQ "), IE(
				"IRELAND "), IM("ISLE OF MAN "), IL("ISRAEL "), IT("ITALY "), JM(
				"JAMAICA "), JP("JAPAN "), JE("JERSEY "), JO("JORDAN "), KZ(
				"KAZAKHSTAN "), KE("KENYA "), KI("KIRIBATI "), KP(
				"KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF "), KR(
				"KOREA, REPUBLIC OF "), KW("KUWAIT "), KG("KYRGYZSTAN "), LA(
				"LAO PEOPLE'S DEMOCRATIC REPUBLIC "), LV("LATVIA "), LB(
				"LEBANON "), LS("LESOTHO "), LR("LIBERIA "), LY(
				"LIBYAN ARAB JAMAHIRIYA "), LI("LIECHTENSTEIN "), LT(
				"LITHUANIA "), LU("LUXEMBOURG "), MO("MACAO "), MK(
				"MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF "), MG(
				"MADAGASCAR "), MW("MALAWI "), MY("MALAYSIA "), MV("MALDIVES "), ML(
				"MALI "), MT("MALTA "), MH("MARSHALL ISLANDS "), MQ(
				"MARTINIQUE "), MR("MAURITANIA "), MU("MAURITIUS "), YT(
				"MAYOTTE "), MX("MEXICO "), FM(
				"MICRONESIA, FEDERATED STATES OF "), MD("MOLDOVA, REPUBLIC OF "), MC(
				"MONACO "), MN("MONGOLIA "), ME("MONTENEGRO "), MS(
				"MONTSERRAT "), MA("MOROCCO "), MZ("MOZAMBIQUE "), MM(
				"MYANMAR "), NA("NAMIBIA "), NR("NAURU "), NP("NEPAL "), NL(
				"NETHERLANDS "), NC("NEW CALEDONIA "), NZ("NEW ZEALAND "), NI(
				"NICARAGUA "), NE("NIGER "), NG("NIGERIA "), NU("NIUE "), NF(
				"NORFOLK ISLAND "), MP("NORTHERN MARIANA ISLANDS "), NO(
				"NORWAY "), OM("OMAN "), PK("PAKISTAN "), PW("PALAU "), PS(
				"PALESTINIAN TERRITORY, OCCUPIED "), PA("PANAMA "), PG(
				"PAPUA NEW GUINEA "), PY("PARAGUAY "), PE("PERU "), PH(
				"PHILIPPINES "), PN("PITCAIRN "), PL("POLAND "), PT("PORTUGAL "), PR(
				"PUERTO RICO "), QA("QATAR "), RE("REUNION "), RO("ROMANIA "), RU(
				"RUSSIAN FEDERATION "), RW("RWANDA "), BL("SAINT BARTHELEMY "), SH(
				"SAINT HELENA, ASCENSION AND TRISTAN DA CUNHA "), KN(
				"SAINT KITTS AND NEVIS "), LC("SAINT LUCIA "), MF(
				"SAINT MARTIN (FRENCH PART) "), PM("SAINT PIERRE AND MIQUELON "), VC(
				"SAINT VINCENT AND THE GRENADINES "), WS("SAMOA "), SM(
				"SAN MARINO "), ST("SAO TOME AND PRINCIPE "), SA(
				"SAUDI ARABIA "), SN("SENEGAL "), RS("SERBIA "), SC(
				"SEYCHELLES "), SL("SIERRA LEONE "), SG("SINGAPORE "), SX(
				"SINT MAARTEN (DUTCH PART) "), SK("SLOVAKIA "), SI("SLOVENIA "), SB(
				"SOLOMON ISLANDS "), SO("SOMALIA "), ZA("SOUTH AFRICA "), GS(
				"SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS "), ES("SPAIN "), LK(
				"SRI LANKA "), SD("SUDAN "), SR("SURINAME "), SJ(
				"SVALBARD AND JAN MAYEN "), SZ("SWAZILAND "), SE("SWEDEN "), CH(
				"SWITZERLAND "), SY("SYRIAN ARAB REPUBLIC "), TW(
				"TAIWAN, PROVINCE OF CHINA "), TJ("TAJIKISTAN "), TZ(
				"TANZANIA, UNITED REPUBLIC OF "), TH("THAILAND "), TL(
				"TIMOR-LESTE "), TG("TOGO "), TK("TOKELAU "), TO("TONGA "), TT(
				"TRINIDAD AND TOBAGO "), TN("TUNISIA "), TR("TURKEY "), TM(
				"TURKMENISTAN "), TC("TURKS AND CAICOS ISLANDS "), TV("TUVALU "), UG(
				"UGANDA "), UA("UKRAINE "), AE("UNITED ARAB EMIRATES "), GB(
				"UNITED KINGDOM "), US("UNITED STATES "), UM(
				"UNITED STATES MINOR OUTLYING ISLANDS "), UY("URUGUAY "), UZ(
				"UZBEKISTAN "), VU("VANUATU "), VE(
				"VENEZUELA, BOLIVARIAN REPUBLIC OF "), VN("VIET NAM "), VG(
				"VIRGIN ISLANDS, BRITISH "), VI("VIRGIN ISLANDS, U.S. "), WF(
				"WALLIS AND FUTUNA "), EH("WESTERN SAHARA "), YE("YEMEN "), ZM(
				"ZAMBIA "), ZW("ZIMBABWE "), ZZ("NA"), EU("European Union");
		private String name;

		Country(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	// Base85 Decoding table
	private final static char[] base85 = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z', '.', ',', ';', '\'', '"', '`', '<', '>', '{', '}',
			'[', ']', '=', '+', '-', '~', '*', '@', '#', '%', '$', '&', '!',
			'?' };

	/**
	 * Constructs a new {@link IPConverter}
	 * 
	 * @param node
	 *            reference to freenet {@link Node}
	 */
	private IPConverter(File dbFile) {
		this.dbFile = dbFile;
	}

	/**
	 * Returns the reference to singleton object of this class.
	 * 
	 * @return singleton object
	 */
	public static IPConverter getInstance(File file) {
		if (instance == null) {
			instance = new IPConverter(file);
		} else if(!instance.getDBFile().equals(file)) {
			instance = new IPConverter(file);
		}
		return instance;
	}

	/**
	 * Copies all IP ranges from given String to memory as a
	 * {@link WeakReference}.
	 * 
	 * @param line
	 *            {@link String} containing IP ranges
	 * @throws IOException
	 */
	private Cache readRanges() {
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(dbFile, "r");
			String line;
			do {
				line = raf.readLine();
			} while (!line.startsWith(START));
			// Remove ##start##
			line = line.substring(START.length());
			// Count of entries (each being 7 Bytes)
			int size = line.length() / 7;
			// Arrays to form a Cache
			short[] codes = new short[size];
			long[] ips = new long[size];
			// Read ips and add it to ip table
			for (int i = 0; i < size; i++) {
				int offset = i * 7;
				String iprange = line.substring(offset, offset + 7);
				// Code
				String code = iprange.substring(0, 2);
				// Ip
				String ipcode = iprange.substring(2);
				long ip = decodeBase85(ipcode.getBytes());
				Country country = Country.valueOf(code);
				codes[i] = (short) country.ordinal();
				ips[i] = ip;
			}
			raf.close();
			return new Cache(codes, ips);
		} catch (FileNotFoundException e) {
			// Not downloaded yet
			Logger.warning(this, "Database file not found!", e);
		} catch (IOException e) {
			Logger.error(this, e.getMessage());
		}
		return null;
	}

	/**
	 * Converts a given IP4 in a long number
	 * 
	 * @param ip
	 *            IP in "XX.XX.XX.XX" format
	 * @return IP in long format
	 * @throws NumberFormatException If the string is not an IP address.
	 */
	public long ip2num(String ip) {
		String[] split = ip.split("\\.");
		if(split.length != 4) throw new NumberFormatException();
		long num = 0;
		long coef = (256 << 16);
		for (int i = 0; i < split.length; i++) {
			long modulo = Integer.parseInt(split[i]) % 256;
			num += (modulo * coef);
			coef >>= 8;
		}
		return num;
	}

	/**
	 * Returns a {@link Country} respecting given IP4.
	 * 
	 * @param ip
	 *            IP in "XX.XX.XX.XX" format
	 * @return {@link Country} of given IP, or null if the passed in string is
	 * not an IP address or we fail to load the ip to country data file.
	 * @throws IOException
	 */
	public Country locateIP(String ip) {
		if(ip == null) return null;
		Cache memCache = getCache();
		long longip;
		try {
			longip = ip2num(ip);
		} catch (NumberFormatException e) {
			return null; // Not an IP address.
		}
		// Check cache first
		if (cache.containsKey(longip)) {
			return cache.get(longip);
		}
		if(memCache == null) return null;
		long[] ips = memCache.getIps();
		short[] codes = memCache.getCodes();
		// Binary search
		int start = 0;
		int last = ips.length - 1;
		int mid;
		while ((mid = Math.round((last - start) / 2)) > 0) {
			int midpos = mid + start;
			if (longip >= ips[midpos]) {
				last = midpos;
			} else {
				start = midpos;
			}
		}
		short countryOrdinal = codes[last];
		Country country = Country.values()[countryOrdinal];
		cache.put(longip, country);
		return country;
	}

	/**
	 * Returns {@link Cache} containing IPranges
	 * 
	 * @return {@link Cache}
	 */
	private Cache getCache() {
		Cache memCache = null;
		synchronized (IPConverter.class) {
			if(fullCache != null)
				memCache = fullCache.get();
			if(memCache == null) {
				fullCache = new WeakReference<Cache>(memCache = readRanges());
			}
		}
		return memCache;
	}

	/**
	 * Decodes a ASCII85 code into a long number.
	 * 
	 * @param code
	 *            encoded bytes
	 * @return decoded long
	 */
	private long decodeBase85(byte[] code) {
		long result = 0;
		int base = base85.length;
		if (code.length != 5)
			return result;
		long coef = 1;
		for (int i = 4; i >= 0; i--) {
			Integer value = getBaseIndex(code[i]);
			result += value * coef;
			coef *= base;
		}
		return result;
	}

	/**
	 * Returns index of given character in base85 table, used for decoding.
	 * 
	 * @param c
	 *            Character to find index
	 * @return index of given char
	 * @see #decodeBase85(byte[])
	 */
	private int getBaseIndex(byte c) {
		for (int i = 0; i < base85.length; i++) {
			if (c == base85[i])
				return i;
		}
		return -1;
	}
	
	/**
	 * Returns database file containing IP ranges
	 * @return database file
	 */
	File getDBFile() {
		return this.dbFile;
	}
}