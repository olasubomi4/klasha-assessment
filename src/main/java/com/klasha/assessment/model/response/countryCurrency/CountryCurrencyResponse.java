package com.klasha.assessment.model.response.countryCurrency;

import lombok.Data;

@Data
public class CountryCurrencyResponse {
    private boolean error;
    private String msg;
    private CountryCurrencyData data;

    public String getIso3()
    {
        if(this.isError())
        {
            throw new RuntimeException("From object");

        }
        else if(this.data!=null)
        {
            return this.data.getIso3();
        }
        throw new RuntimeException("From object");
    }
    public String getIso2()
    {
        if(this.isError())
        {
            throw new RuntimeException("From object");

        }
        else if(this.data!=null)
        {
            return this.data.getIso2();
        }
        throw new RuntimeException("From object");
    }
    public String getCurrency()
    {
        if(this.isError())
        {
            throw new RuntimeException("From object");

        }
        else if(this.data!=null)
        {
            return this.data.getCurrency();
        }
        throw new RuntimeException("From object");
    }


}
