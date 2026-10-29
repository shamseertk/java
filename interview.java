public class interview {

  static int findMemoryStackCount(int n, int k, int[][] queries) {

    if(queries == null || queries.length == 0 || (queries.length > 0 && queries[0].length == 0))
      return 0;
    int calc[] = new int[n];
    for(int i=0;i<queries.length;i++) {
      for(int j=queries[i][0]; j<queries[i][1] && j<=n;j++){
        calc[j] = calc[j] + queries[i][2];
      }
    }

    int result = 0;
    for(int x=0;x<calc.length;x++){

      if(calc[x] > k){
        result++;
      }
    }
    return result;
  }

  public static void main(String[] args) {
    int n = 7;
    int k = 10;
    int[][] queries = {
      {1, 4, 5},
      {2, 6, 6},
      {4, 7, 4}
    };
    int result = findMemoryStackCount(n, k, queries);
  }
}
