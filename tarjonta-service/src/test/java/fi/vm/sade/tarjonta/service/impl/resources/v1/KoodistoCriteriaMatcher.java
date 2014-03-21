package fi.vm.sade.tarjonta.service.impl.resources.v1;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;

public class KoodistoCriteriaMatcher implements
        Matcher<SearchKoodisCriteriaType> {

    private String uri;

    public KoodistoCriteriaMatcher(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean matches(Object arg0) {
        SearchKoodisCriteriaType type = (SearchKoodisCriteriaType) arg0;
        return type != null && type.getKoodiUris().contains(uri);
    }

    @Override
    public void describeTo(Description arg0) {
    }

    @Override
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
    }
}