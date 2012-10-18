package fi.vm.sade.tarjonta.service.impl.exceptionwrapper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import fi.vm.sade.generic.service.exception.AbstractFaultWrapper;
import fi.vm.sade.generic.service.exception.SadeBusinessException;
import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.service.types.tarjonta.GenericFaultInfo;

@Aspect
public class ExceptionWrappingAspect extends AbstractFaultWrapper<GenericFault> {

    public ExceptionWrappingAspect() {
        super(GenericFault.class);
    }

    /**
     * Defines the pointcut for service interface methods.
     */
    @Pointcut("within(fi.vm.sade.tarjonta.service.impl.*)")
    public void serviceMethod() {
    }

    /**
     * Around-type advice which simply proceeds to join point but catches thrown
     * exceptions and wraps them.
     *
     * @param pjp
     * @return
     * @throws GenericFault
     */
    @Around("serviceMethod()")
    public Object wrapException(ProceedingJoinPoint pjp) throws GenericFault {
        return super.wrapException(pjp);
    }

    @Override
    protected GenericFault createFaultInstance(Throwable ex) {
        String key = "";
        if (ex instanceof SadeBusinessException) {
            key = ((SadeBusinessException) ex).getErrorKey();
        } else {
            key = ex.getClass().getName();
        }

        GenericFaultInfo info = new GenericFaultInfo();
        info.setErrorCode(key);
        info.setExplanation(ex.getMessage());
        return new GenericFault(ex.getMessage(), info);
    }

}
