package com.lateral.lateral.model;

import java.util.ArrayList;

public class BidList {
    private ArrayList<Bid> bids = new ArrayList<Bid>();

    public void add(Bid bid){
        bids.add(bid);
    }

    public boolean has(Bid bid) {
        for (Bid bidInstance: bids){
            if (bidInstance.getId().equals(bid.getId())){
                return true;
            }
        }
        return false;
    }

    public Bid get(Bid bid) {
        for (Bid bidInstance: bids){
            if (bidInstance.getId().equals(bid.getId())){
                return bidInstance;
            }
        }
        throw new IllegalArgumentException("Bid does not exist");
    }

    public void delete(Bid bid){ bids.remove(bid); }

    public ArrayList<Bid> getAll(){ return bids; }

    public int getCount(){ return bids.size(); }

}
