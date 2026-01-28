package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowAttributeSumEliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowestRatingEliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.PublicVoteEliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;
import bg.sofia.uni.fmi.mjt.show.ergenka.HumorousErgenka;
import bg.sofia.uni.fmi.mjt.show.ergenka.RomanticErgenka;

public class Demo {
    public static void main(String[] args) {

        Ergenka e1 = new HumorousErgenka("Ana",(short)12,5,1,17);
        Ergenka e2 = new HumorousErgenka("Bobi",(short)13,19,6,82);
        Ergenka e3 = new HumorousErgenka("Vesi",(short)17,8,17,34);
        Ergenka e4 = new HumorousErgenka("Geri",(short)19,1,61,18);
        Ergenka e5 = new RomanticErgenka("Dora",(short)10,0,21,9, "na grozdober");
        Ergenka e6 = new RomanticErgenka("Elvis",(short)11,2,101,4,"vkushti");
        Ergenka e7 = new RomanticErgenka("Jaki",(short)67,3,16,1, "na razhodka");
        Ergenka e8 = new RomanticErgenka("Krisi",(short)98,4,12,8,"v mazeto");

        String[] votes = {"Krisi","Jaki","Elvis", "Krisi","Krisi","Krisi","Krisi","Elvis"};
        EliminationRule rule1 = new LowAttributeSumEliminationRule(10);
        EliminationRule rule2 = new LowestRatingEliminationRule();
        EliminationRule rule3 = new PublicVoteEliminationRule(votes);
        EliminationRule rule4 = new LowestRatingEliminationRule();

        EliminationRule[] defaultRules = {rule1, rule2, rule3, rule4};
        Ergenka[] ergenkas = {e1, e2, e3, e4, e5, e6, e7, e8};
        ShowAPI moderator = new ShowAPIImpl(ergenkas, defaultRules);

        moderator.eliminateErgenkas(new EliminationRule[0]);
        ergenkas = moderator.getErgenkas();
        for(Ergenka each : ergenkas){
            System.out.println(each.getName() + " " + each.getAge() + " " + each.getRomanceLevel() + " " + each.getHumorLevel() + " " + each.getRating());
        }
    }
}