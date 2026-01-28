package bg.sofia.uni.fmi.mjt.show.elimination;

import java.util.Arrays;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class PublicVoteEliminationRule implements EliminationRule 
{
    private String[] votes;
    
    public PublicVoteEliminationRule(String[] votes)
    {
        this.votes = new String[0];
        if(votes != null)
            this.votes = Arrays.copyOf(votes, votes.length);
    }

    public String[] getVotes()
    {
        return votes;
    }

    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas)
    {
        if(ergenkas == null)
            return new Ergenka[0];

        int countToEliminate = 0;
        int[] countVotes = new int[ergenkas.length];
        Arrays.fill(countVotes, 0);

        for(int i=0; i<ergenkas.length; i++)
        {
            for(String v : votes)
            {
                if (v.equals(ergenkas[i].getName()))
                    countVotes[i]++;
            }
        }

        for(int i=0; i<ergenkas.length; i++)
        {
            if(countVotes[i] > ergenkas.length/2 +1)
                countToEliminate++;
        }

        if(countToEliminate == 0)
            return ergenkas;

        Ergenka[] nonEliminated = new Ergenka[ergenkas.length - 1];

         for(int i = 0, j = 0; i < nonEliminated.length ; i++)
        {
            if(countVotes[i] < (ergenkas.length/2) +1){
                nonEliminated[j] = ergenkas[i];
                j++;
            }
        }  

        return nonEliminated;
    }
    
}