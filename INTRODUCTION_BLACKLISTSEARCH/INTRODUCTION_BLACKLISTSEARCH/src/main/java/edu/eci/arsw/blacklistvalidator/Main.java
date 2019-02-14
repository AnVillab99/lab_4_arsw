/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        System.out.println("cuantos threads usara");
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        
        //List<Integer> blackListOcurrences=hblv.checkHost("212.24.24.55",n);
        List<Integer> blackListOcurrences=hblv.checkHost("200.24.34.55",n);
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences);
        System.exit(0);
        
    }
    
}
