package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowestRatingEliminationRule implements EliminationRule 
{
    public LowestRatingEliminationRule()
    {

    }

    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas)
    {
        if(ergenkas == null)
            return new Ergenka[0];
        
        int minRating = Integer.MAX_VALUE;
        int countToEliminate = 0;
        
        for(Ergenka e: ergenkas)
        {
            if(e.getRating() < minRating)
                minRating = e.getRating();
        }    
        for(Ergenka e: ergenkas)
        {
            if(e.getRating() == minRating)
                countToEliminate++;
        }  

        Ergenka[] nonEliminated = new Ergenka[ergenkas.length - countToEliminate];

         for(int i = 0, j = 0; i < nonEliminated.length ; i++)
        {
            if(ergenkas[i].getRating() > minRating)
                nonEliminated[j++] = ergenkas[i];
        }  

        return nonEliminated;
    }
    
}