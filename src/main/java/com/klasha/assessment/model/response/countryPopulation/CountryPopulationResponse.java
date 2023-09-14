package com.klasha.assessment.model.response.countryPopulation;

import lombok.Data;

@Data
public class CountryPopulationResponse {
    private boolean error;
    private String msg;
    private CountryPopulationData data;

    public String getCountry()
    {
        if(this.isError())
        {
            throw new RuntimeException("From object");

        }
        else if(this.data!=null)
        {
            return this.data.getCountry();
        }
        throw new RuntimeException("From object");
    }
    public Long getPopulation()
    {
        if(this.isError())
        {
            throw new RuntimeException("From object");

        }
        else if(this.data!=null&&this.data.getPopulationCounts().size()>0)
        {
            return this.data.getPopulationCounts().get(this.data.getPopulationCounts().size()-1).getValue();
        }
        throw new RuntimeException("From object");
    }
}
