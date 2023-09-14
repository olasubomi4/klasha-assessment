package com.klasha.assessment.model.response.filterCitiesAndPopulation;

import lombok.Data;

import java.util.List;

@Data
public class FilterCitiesAndPopulationResponse {
    private boolean error;
    private String msg;
    private List<FilterCitiesAndPopulationCityInfo> data;

    public String getCountry()
    {
        if(this.isError())
        {
            return null;
        }
        else if(this.data!=null&&!this.data.isEmpty()&&this.data.get(0)!=null)
        {
            return this.data.get(0).getCountry();
        }
        return null;
    }
    public Long getPopulationValue(Integer index)
    {
        if(this.isError())
        {
            return null;

        }
        else if(this.data!=null&&!this.data.isEmpty()&&this.data.get(index)!=null&& !this.data.get(index).getPopulationCounts().isEmpty())
        {
            return this.data.get(index).getPopulationCounts().get(0).getValue();
        }
        return null;
    }

    public String getCity(Integer index)
    {
        if(this.isError())
        {
            return null;

        }
        else if(this.data!=null&&!this.data.isEmpty()&&this.data.get(index)!=null)
        {
            return this.data.get(index).getCity();
        }
        return null;
    }

    public Integer getDataSize()
    {
        if(this.isError())
        {
            return 0;

        }
        else if(this.data!=null&& !this.data.isEmpty())
        {
            return this.data.size();
        }
        return 0;
    }

}
