package fi.vm.sade.tarjonta.service.resources.v1;

import java.util.ArrayList;
import java.util.List;

//HAUN HAKUEHTO
public class HakuSearchCriteria {

    public static class Builder {
        List<HakuSearchCriteria> criteria = new ArrayList<HakuSearchCriteria>();

        public List<HakuSearchCriteria> build() {
            return criteria;
        }

        public Builder mustMatch(HakuSearchCriteria.Field field, Object value) {
            criteria.add(new HakuSearchCriteria(field, value, Match.MUST_MATCH));
            return this;
        }

        public Builder like(Field field, Object value) {
            criteria.add(new HakuSearchCriteria(field, value, Match.LIKE));
            return this;
        }
        
        public Builder add(Field field, Object value, Match match){
            criteria.add(new HakuSearchCriteria(field, value, match));
            return this;
        }
    }

    public enum Field {
        TILA,
        HAKUKAUSI,
        HAKUVUOSI,
        KOULUTUKSEN_ALKAMISKAUSI,
        KOULUTUKSEN_ALKAMISVUOSI,
        HAKUTAPA,
        HAKUTYYPPI,
        KOHDEJOUKKO,
        HAKUSANA,
        TARJOAJAOID
    }

    public enum Match {
        MUST_MATCH,
        MUST_NOT,
        LIKE,
        LIKE_OR
    }

    @Override
    public String toString() {
        return "HakuSearchCriteria [match=" + match + ", field=" + field
                + ", value=" + value + "]";
    }

    private Match match;

    private Field field;

    private Object value;

    private String raw;

    private HakuSearchCriteria(Field field, Object value, Match match) {
        this.setMatch(match);
        this.setField(field);
        this.setValue(value);
    }

    public HakuSearchCriteria(String sParam) {
        this.raw = sParam;
    }

    public Field getField() {
        return field;
    }

    public Match getMatch() {
        return match;
    }

    public String getRaw() {
        return raw;
    }

    public Object getValue() {
        return value;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
