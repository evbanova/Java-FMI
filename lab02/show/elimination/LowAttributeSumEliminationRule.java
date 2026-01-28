package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowAttributeSumEliminationRule implements EliminationRule 
{
    private int threshold = 0;

    public LowAttributeSumEliminationRule(int threshold)
    {
        if(threshold >= 0)
            this.threshold = threshold;
    }

    public int getThreshold()
    {
        return threshold;
    }

    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas)
    {
        if(ergenkas == null)
            return new Ergenka[0];

        int countToEliminate = 0;
        
        for(Ergenka e: ergenkas)
        {
            if(e.getRomanceLevel() + e.getHumorLevel() <= threshold)
                countToEliminate++;
        }     

        Ergenka[] nonEliminated = new Ergenka[ergenkas.length - countToEliminate];

         for(int i = 0, j = 0; i < nonEliminated.length ; i++)
        {
            if(ergenkas[i].getRomanceLevel() + ergenkas[i].getHumorLevel() > threshold)
                nonEliminated[j++] = ergenkas[i];
        }  

        return nonEliminated;
    }
    
}
