package jlox.interpreters.lox;

@SuppressWarnings("serial")
public abstract class Propogator extends RuntimeException {

    public Propogator() {
        super(null, null, false, false);//disable stacktraces. We don't need them
    }
    
    
    public static class BreakPropogator extends Propogator {

        /**
         * 
         */
        private static final long serialVersionUID = 6405573001713741734L;
        
        
        BreakPropogator(){
            super();
        }
    }
    
    
    public static class ReturnPropogator extends Propogator {
        /**
         * 
         */
        private static final long serialVersionUID = 6405573001713741734L;
        
        final Object value;
        
        ReturnPropogator(Object value){
            super();
            this.value = value;
        }
    }
    
    public static class ContinuePropogator extends Propogator {
        ContinuePropogator(){
            super();
        }
    }
}
