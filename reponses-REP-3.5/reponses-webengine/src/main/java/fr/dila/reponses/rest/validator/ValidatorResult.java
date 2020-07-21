package fr.dila.reponses.rest.validator;

public class ValidatorResult {
    
    private boolean isValid;
    
    private String errorMsg;    

    public static final ValidatorResult RESULT_OK = new ValidatorResult(true);
    
    public ValidatorResult(boolean isValid, String errorMsg){
        this.isValid = isValid;
        this.errorMsg = errorMsg;
    }
    
    public ValidatorResult(boolean isValid){
        this(isValid, null);
    }
    
    public boolean isValid() {
        return isValid;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public static ValidatorResult error(String msg){
        return new ValidatorResult(false, msg);
    }
}
