package com.klasha.assessment.model.response.countryLocation;

import lombok.Data;

@Data
public class CountryLocationResponse {
    private boolean error;
    private String msg;
    private CountryLocationData data;



    public Double getLatitude()
    {
        if(this.isError())
        {
            throw new RuntimeException("From object");

        }
        else if(this.data!=null)
        {
            return this.data.getLatitude();
        }
        throw new RuntimeException("From object");
    }

    public Double getLongitude()
    {
        if(this.isError())
        {
            throw new RuntimeException("From object");

        }
        else if(this.data!=null)
        {
            return this.data.getLongitude();
        }
        throw new RuntimeException("From object");
    }

}
