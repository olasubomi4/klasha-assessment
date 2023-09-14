package com.klasha.assessment.model.response.countryCapital;

import lombok.Data;

@Data
public class CountryCapitalResponse {
    private boolean error;
    private String msg;
    private CountryCapitalData data;

    public String getCapital()
    {
        if(this.isError())
        {
            throw new RuntimeException("From object");

        }
        else if(this.data!=null)
        {
            return this.data.getCapital();
        }
        throw new RuntimeException("From object");
    }
}
