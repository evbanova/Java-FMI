package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class HumorousErgenka implements Ergenka
{
    
    private String name = "";
    private short age = 0;
    private int romanceLevel = 0;
    private int humorLevel = 0;
    private int rating = 0;
    
    public HumorousErgenka(String name, short age, int romanceLevel, int humorLevel, int rating)
    {
        if(name!=null)
            this.name = name;
        if(age > 0)    
            this.age = age;
        if(humorLevel > 0)
            this.humorLevel = humorLevel;
        if(romanceLevel > 0)
            this.romanceLevel = romanceLevel;
        if(rating > 0)
            this.rating = rating;
    }

    public String getName()
    {
        return name;
    }

    public short getAge()
    {
        return age;
    }
   
    public int getRomanceLevel()
    {
        return romanceLevel;
    }

    public int getHumorLevel()
    {
        return humorLevel;
    }

    public int getRating()
    {
        return rating;
    }

    public void reactToDate(DateEvent dateEvent)
    {
        int bonus = 0;
        if(dateEvent.getDuration() < 30)
            bonus = -2;
        else if (dateEvent.getDuration() > 90)    
            bonus = -3;
        else if (dateEvent.getDuration()>= 30 && dateEvent.getDuration()<=90)
            bonus = 4;    
        
        rating = (humorLevel*5)/dateEvent.getTensionLevel() +  Math.floorDiv(romanceLevel, 3) + bonus;  
    }
}
