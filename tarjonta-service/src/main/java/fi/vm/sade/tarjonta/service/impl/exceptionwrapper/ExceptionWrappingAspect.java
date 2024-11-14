package fi.vm.sade.tarjonta.service.impl.exceptionwrapper;

import fi.vm.sade.generic.service.exception.AbstractFaultWrapper;
import fi.vm.sade.generic.service.exception.SadeBusinessException;
import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.service.types.GenericFaultInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class ExceptionWrappingAspect extends AbstractFaultWrapper<GenericFault> {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  public ExceptionWrappingAspect() {
    super(GenericFault.class);
  }

  /** Defines the pointcut for service interface methods. */
  @Pointcut("within(fi.vm.sade.tarjonta.service.impl.*)")
  public void serviceMethod() {}

  /**
   * Around-type advice which simply proceeds to join point but catches thrown exceptions and wraps
   * them.
   *
   * @param pjp
   * @return
   * @throws GenericFault
   */
  @Around("serviceMethod()")
  public Object wrapException(ProceedingJoinPoint pjp) throws GenericFault {
    try {
      return super.wrapException(pjp);
    } catch (GenericFault e) {
      throw e;
    } catch (RuntimeException e) {
      log.warn("Unwrapped runtime exception occured", e);
      throw e;
    }
  }

  @Override
  protected GenericFault createFaultInstance(Throwable ex) {
    log.warn("Service exception occured", ex);

    String key =
        (ex instanceof SadeBusinessException)
            ? ((SadeBusinessException) ex).getErrorKey()
            : ex.getClass().getName();

    GenericFaultInfo info = new GenericFaultInfo();
    info.setErrorCode(key);
    info.setExplanation(ex.getMessage());
    return new GenericFault(ex.getMessage(), info);
  }
}
