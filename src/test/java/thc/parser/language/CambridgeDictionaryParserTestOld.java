package thc.parser.language;

import org.hamcrest.Matchers;
import org.junit.Test;
import thc.domain.DictionaryResult;

import java.util.Optional;

import static org.junit.Assert.*;

public class CambridgeDictionaryParserTestOld {

	@Test
	public void testParseBanana() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("banana").old_parse();

		assertTrue(result.isPresent());
		assertEquals("bəˈnɑː.nə", result.get().IPA);
		assertThat(result.get().pronunciationUrl, Matchers.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukb/ukbal/ukballs018.mp3"));
	}

	@Test
	public void testParseOrange() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("orange").old_parse();

		assertTrue(result.isPresent());
		assertEquals("ˈɒr.ɪndʒ",result.get().IPA);
		assertThat(result.get().pronunciationUrl, Matchers.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/uko/ukora/ukorang001.mp3"));
	}

	@Test
	public void testParseLemon() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("lemon").old_parse();

		assertTrue(result.isPresent());
		assertEquals("ˈlem.ən",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukl/uklei/ukleisu005.mp3"));
	}

	@Test
	public void testParseStar() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("star").old_parse();

		assertTrue(result.isPresent());
		assertEquals("stɑː",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/uks/uksta/ukstand022.mp3"));
	}

	@Test
	public void testParsePeak() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("peak").old_parse();

		assertTrue(result.isPresent());
		assertEquals("piːk",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukp/ukpay/ukpayro024.mp3"));
	}

	@Test
	public void testParseFit() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("fit").old_parse();

		assertTrue(result.isPresent());
		assertEquals("fɪt",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukf/ukfis/ukfistf003.mp3"));
	}

	@Test
	public void testParsePet() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("pet").old_parse();

		assertTrue(result.isPresent());
		assertEquals("pet",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukp/ukper/ukperv_026.mp3"));
	}

	@Test
	public void testParseCat() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("cat").old_parse();

		assertTrue(result.isPresent());
		assertEquals("kæt",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukc/ukcas/ukcaste011.mp3"));
	}

	@Test
	public void testParseFrog() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("frog").old_parse();

		assertTrue(result.isPresent());
		assertEquals("frɒɡ",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukf/ukfri/ukfrill022.mp3"));
	}

	@Test
	public void testParseCar() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("car").old_parse();

		assertTrue(result.isPresent());
		assertEquals("kɑː",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukc/ukcap/ukcapit027.mp3"));
	}

	@Test
	public void testParseTall() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("tall").old_parse();

		assertTrue(result.isPresent());
		assertEquals("tɔːl",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukt/uktaj/uktajik024.mp3"));
	}

	@Test
	public void testParseFoot() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("foot").old_parse();

		assertTrue(result.isPresent());
		assertEquals("fʊt",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukf/ukfol/ukfolks026.mp3"));
	}

	@Test
	public void testParseRoom() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("room").old_parse();

		assertTrue(result.isPresent());
		assertEquals("ruːm",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukr/ukroo/ukrooke003.mp3"));
	}

	@Test
	public void testParseLuck() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("luck").old_parse();

		assertTrue(result.isPresent());
		assertEquals("lʌk",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukl/ukloy/ukloyal030.mp3"));
	}

	@Test
	public void testParseBird() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("bird").old_parse();

		assertTrue(result.isPresent());
		assertEquals("bɜːd",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukb/ukbip/ukbipla004.mp3"));
	}

	@Test
	public void testParseName() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("name").old_parse();

		assertTrue(result.isPresent());
		assertEquals("neɪm",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukm/ukmyt/ukmyth_029.mp3"));
	}

	@Test
	public void testParseKite() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("kite").old_parse();

		assertTrue(result.isPresent());
		assertEquals("kaɪt",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukk/ukkit/ukkit__004.mp3"));
	}

	@Test
	public void testParseCow() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("cow").old_parse();

		assertTrue(result.isPresent());
		assertEquals("kaʊ",result.get().IPA);
		assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/english/uk_pron/u/ukc/ukcov/ukcover011.mp3"));
	}

	@Test
	public void testParseToy() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("toy").old_parse();

		assertTrue(result.isPresent());
		assertEquals("tɔɪ",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/t/toy/toy__/toy.mp3"));
	}

	@Test
	public void testParseRoad() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("road").old_parse();

		assertTrue(result.isPresent());
		assertEquals("rəʊd",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/r/roa/road_/road.mp3"));
	}

	@Test
	public void testParseTear() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("tear").old_parse();

		assertTrue(result.isPresent());
		assertEquals("teə",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/t/tea/tear_/tear_03_00.mp3"));
	}

	@Test
	public void testParsePair() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("pair").old_parse();

		assertTrue(result.isPresent());
		assertEquals("peə",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/p/pai/pair_/pair.mp3"));
	}

	@Test
	public void testParsePoor() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("poor").old_parse();

		assertTrue(result.isPresent());
		assertEquals("pɔː",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/p/poo/poor_/poor.mp3"));
	}

	@Test
	public void testParseFire() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("fire").old_parse();

		assertTrue(result.isPresent());
		assertEquals("faɪə",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/f/fir/fire_/fire.mp3"));
	}

	@Test
	public void testParsePower() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("power").old_parse();

		assertTrue(result.isPresent());
		assertEquals("paʊə",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/p/pow/power/power.mp3"));
	}

	@Test
	public void testParseThin() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("thin").old_parse();

		assertTrue(result.isPresent());
		assertEquals("θɪn",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/t/thi/thin_/thin.mp3"));
	}

	@Test
	public void testParseShine() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("shine").old_parse();

		assertTrue(result.isPresent());
		assertEquals("ʃaɪn",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/s/shi/shine/shine.mp3"));
	}

	@Test
	public void testParseHead() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("head").old_parse();

		assertTrue(result.isPresent());
		assertEquals("hed",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/h/hea/head_/head.mp3"));
	}

	@Test
	public void testParseChurch() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("church").old_parse();

		assertTrue(result.isPresent());
		assertEquals("tʃɜːtʃ",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/c/chu/churc/church.mp3"));
	}

	@Test
	public void testParseWild() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("wild").old_parse();

		assertTrue(result.isPresent());
		assertEquals("waɪld",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/w/wil/wild_/wild.mp3"));
	}

	@Test
	public void testParseSing() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("sing").old_parse();

		assertTrue(result.isPresent());
		assertEquals("sɪŋ",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/s/sin/sing_/sing.mp3"));
	}

	@Test
	public void testParseVote() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("vote").old_parse();

		assertTrue(result.isPresent());
		assertEquals("vəʊt",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/v/vot/vote_/vote.mp3"));
	}

	@Test
	public void testParseThey() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("they").old_parse();

		assertTrue(result.isPresent());
		assertEquals("ðeɪ",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/t/the/they_/they.mp3"));
	}

	@Test
	public void testParseZinc() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("zinc").old_parse();

		assertTrue(result.isPresent());
		assertEquals("zɪŋk",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/z/zin/zinc_/zinc.mp3"));
	}

	@Test
	public void testParseMeasure() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("measure").old_parse();

		assertTrue(result.isPresent());
		assertEquals("ˈmeʒ.ə",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/m/mea/measu/measure.mp3"));
	}

	@Test
	public void testParseJoke() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("joke").old_parse();

		assertTrue(result.isPresent());
		assertEquals("dʒəʊk",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/j/jok/joke_/joke.mp3"));
	}

	@Test
	public void testParseYet() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("yet").old_parse();

		assertTrue(result.isPresent());
		assertEquals("jet",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/y/yet/yet__/yet.mp3"));
	}

	@Test
	public void testParseBusstop() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("bus-stop").old_parse();

		assertTrue(result.isPresent());
		assertEquals("ˈbʌs ˌstɒp",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/u/usc/uscld/uscld00151.mp3"));
	}

	@Test
	public void testParseFebruary() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("february").old_parse();

		assertTrue(result.isPresent());
		assertEquals("ˈfeb.ru.r.i",result.get().IPA);
		//assertTrue(result.get().pronunciationUrl.endsWith("//dictionary.cambridge.org/media/british/us_pron/e/eus/eus71/eus71794.mp3"));
	}

	@Test
	public void testParseFailGetContent() {
		Optional<DictionaryResult> result = new CambridgeDictionaryParser("asdlkjfwerj").old_parse();

		assertTrue(!result.isPresent());
	}
}
