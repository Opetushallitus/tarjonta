package fi.vm.sade.tarjonta.matchers;

import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class KoodistoCriteriaMatcher extends BaseMatcher<SearchKoodisCriteriaType> {

    private String uri;

    public KoodistoCriteriaMatcher(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean matches(Object arg0) {
        SearchKoodisCriteriaType koodisCriteriaType = (SearchKoodisCriteriaType) arg0;
        return koodisCriteriaType != null && koodisCriteriaType.getKoodiUris().contains(uri);
    }

    @Override
    public void describeTo(Description arg0) {
    }

}
