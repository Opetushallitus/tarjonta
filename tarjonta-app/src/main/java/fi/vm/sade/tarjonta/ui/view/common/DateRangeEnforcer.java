package fi.vm.sade.tarjonta.ui.view.common;

import java.util.Date;

import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.DateField;

public class DateRangeEnforcer implements Listener {

	private static final long serialVersionUID = 1L;

	private final DateField first;
	private final DateField last;
	
	public DateRangeEnforcer(DateField first, DateField last) {
		super();
		this.first = first;
		this.last = last;
		first.addListener(this);
		last.addListener(this);
	}

	@Override
	public void componentEvent(Event event) {
		Date a = (Date) first.getValue();
		Date b = (Date) last.getValue();
		
		if (a==null || b==null || b.equals(a) || b.after(a)) {
			return;
		}
		
		last.setValue(a);
		first.setValue(b);
	}

}
