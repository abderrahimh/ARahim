
//This file is part of LTW.
//Copyright (c) by
//Abderrahim Hechachena <abderrahimhechachena@yahoo.co.uk>
  
//LTW is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//LTW is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details <http://www.gnu.org/licenses/>.

public int[] LeagueTableWeights(char[] arr)
{
  int strLen = arr.Length / 2; // arr is in fact two arrays concatinated to avoid reference complexities
  int[] finalIndex = new int[strLen]; // where final weights are stored it is equal to input array
  int strLen_1 = strLen - 1; // strLen -1
  int strLen_2 = strLen - 2;
  int strLen_1_s = 0;  // strLen - 1 - s
  int flag = 0;  // to carry previous sign
  int flag1 = 0; // to remember previus action
  int ipS = 0;   // i + s
  int ip1 = 0;   // i + 1
  int ipspstrLen = 0; // i + s + strLen
  // calculate first letter weights
  byte[] relPos1 = Encoding.ASCII.GetBytes(arr);
  for (int i = 0; i < strLen; i++) { finalIndex[i] = strLen * (int)relPos1[i]; }
  // loop through othe cells
  for (int s = 1; s < strLen_1; s++)
  {
    strLen_1_s = strLen_1 - s;
    ipS = strLen_2 + s;
    for (int i = strLen_2; i > strLen_1_s; i--)
    {  
      if (arr[i] == arr[ipS])
      {
        ip1 = i + 1; 
        if (flag1 == ip1) { }
        else
        {
          if (arr[ip1] > arr[ip1 + s]) { flag = 1; } else { flag = -1; }
        }
        ipspstrLen = ipS - strLen;
        finalIndex[i] += flag;
        finalIndex[ipspstrLen] -= flag;
        flag1 = i;
      }
      ipS -= 1;
    }
  }
  return finalIndex;
}