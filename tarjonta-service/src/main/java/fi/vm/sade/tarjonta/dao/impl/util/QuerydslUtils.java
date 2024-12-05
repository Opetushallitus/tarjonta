/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.dao.impl.util;

import com.google.common.base.Preconditions;
import com.querydsl.core.types.dsl.BooleanExpression;

/**
 * @author Jukka Raanamo
 */
public class QuerydslUtils {

  /**
   * Returns expression that is created by concatenating given expression with AND operator. If the
   * left expression is null just right is returned.
   *
   * @param left
   * @param right
   * @return
   */
  public static BooleanExpression and(BooleanExpression left, BooleanExpression right) {
    return (left == null ? right : left.and(right));
  }

  public static BooleanExpression andAll(
      BooleanExpression base, BooleanExpression... optionalExpressions) {
    Preconditions.checkNotNull(base, "Base expression cannot nbe null.");

    if (optionalExpressions == null || optionalExpressions.length == 0) {
      return base;
    }

    for (BooleanExpression be : optionalExpressions) {
      System.err.println("be  : " + be);
      base = and(be, base);
    }

    return base;
  }

  public static BooleanExpression or(
      BooleanExpression base, BooleanExpression optionalExpressions) {
    if (base == null) {
      return optionalExpressions;
    } else {
      base.or(optionalExpressions);
    }

    return base;
  }
}
