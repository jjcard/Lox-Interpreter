package jlox.interpreters.lox;

@SuppressWarnings("serial")
public abstract class Propagator extends RuntimeException {

    public Propagator() {
        super(null, null, false, false);//disable stacktraces. We don't need them
    }
    
    
    public static class BreakPropagator extends Propagator {

        private static final long serialVersionUID = 6405573001713741734L;
        
        
        BreakPropagator(){
            super();
        }
    }
    
    
    public static class ReturnPropagator extends Propagator {

        private static final long serialVersionUID = 6405573001713741734L;
        
        final Object value;
        
        ReturnPropagator(Object value){
            super();
            this.value = value;
        }
    }
    
    public static class ContinuePropagator extends Propagator {
        ContinuePropagator(){
            super();
        }
    }
}
