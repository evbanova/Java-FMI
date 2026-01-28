public class TaskDistributor
{
    public static int minDifference(int[] tasks)
    {
        if (tasks.length == 0)
            return 0;
        if (tasks.length == 1)
            return tasks[0];
        
        //solving this problem with Dynamic programming, using a bool array,
        //where dp[i] is true means that the sum i can be made 
        //using top bottom aproach
        //then we will try to find the closest two sums to shouldBeSum 

        int sum = 0;
        for (int task: tasks)
            sum += task;
        int shouldBeSum = sum / 2;

        boolean[] dp = new boolean[sum + 1];
        dp[0] = true;
        for(int task : tasks)
        {
            for (int i = sum; i >= task; i--)
            {
                if (dp[i- task] == true)
                    dp[i] =true;
            }
        }

        for (int i=shouldBeSum; i>=0; i--)
        {
            if(dp[i] == true)
                return (sum -i) -i;
        }

        return -1;
    }
     public static void main(String[] args)
    {
        System.out.println(minDifference(new int[]{}));
    }
}