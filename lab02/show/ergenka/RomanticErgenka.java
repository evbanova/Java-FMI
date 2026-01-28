package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class RomanticErgenka implements Ergenka
{
    private String name = "";
    private short age = 0;
    private int romanceLevel = 0;
    private int humorLevel = 0;
    private int rating = 0;
    private String favoriteDateLocation = "";
    
    public RomanticErgenka(String name, short age, int romanceLevel, int humorLevel, int rating, String favoriteDateLocation)
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
        if(favoriteDateLocation!=null)
            this.name = favoriteDateLocation;    
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

    public String getFavoriteDateLocation()
    {
        return favoriteDateLocation;
    }


    public void reactToDate(DateEvent dateEvent)
    {
        if(dateEvent == null)
            return;
            
        int bonus = 0;
        if(dateEvent.getLocation().equals(getFavoriteDateLocation()))
            bonus = 5;
        if(dateEvent.getDuration() < 30)
            bonus = -3;
        else if (dateEvent.getDuration() > 120)    
            bonus = -2;
        
        rating = (romanceLevel*7)/dateEvent.getTensionLevel() +  Math.floorDiv(humorLevel, 3) + bonus;    
    }
}
