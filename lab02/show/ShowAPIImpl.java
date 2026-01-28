package bg.sofia.uni.fmi.mjt.show;

import java.util.Arrays;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;
import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;

public class ShowAPIImpl implements ShowAPI
{
    private Ergenka[] ergenkas;
    private EliminationRule[] defaultEliminationRules;

    public ShowAPIImpl(Ergenka[] ergenkas, EliminationRule[] defaultEliminationRules)
    {
       this.ergenkas = new Ergenka[0];
       this.defaultEliminationRules =  new EliminationRule[0];

        if(ergenkas != null)
            this.ergenkas = Arrays.copyOf(ergenkas, ergenkas.length);
        if(defaultEliminationRules != null)    
            this.defaultEliminationRules = Arrays.copyOf(defaultEliminationRules, defaultEliminationRules.length);
    }

    public Ergenka[] getErgenkas()
    {
        return ergenkas;
    }

    public void playRound(DateEvent dateEvent)
    {
        for(Ergenka e : ergenkas)
            organizeDate(e, dateEvent);
    }

    public void eliminateErgenkas(EliminationRule[] eliminationRules)
    {
        if(eliminationRules == null)
            return;

        if (eliminationRules.length == 0)
            eliminationRules = Arrays.copyOf(defaultEliminationRules, defaultEliminationRules.length);

        for(EliminationRule rule : eliminationRules)
            ergenkas = rule.eliminateErgenkas(ergenkas);   

    }

    public void organizeDate(Ergenka ergenka, DateEvent dateEvent)
    {
        ergenka.reactToDate(dateEvent);
    }
}
