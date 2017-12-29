package fi.raah.android.curious_catalog_gatherer.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EditableCard implements Comparable<EditableCard> {
    private String name;
    private List<BlockCodeToAmount> blockCodeToAmounts;
    private int selectedBlockIndex = 0;
    private int difference;

    public EditableCard(String name, List<BlockCodeToAmount> blockCodeToAmounts, int difference) {
        this.name = name;
        this.blockCodeToAmounts = blockCodeToAmounts;
        this.difference = difference;
    }

    public String getName() {
        return name;
    }

    public int getDifference() {
        return difference;
    }

    public List<String> getBlockCodes() {
        List<String> blockCodes = new ArrayList<>();
        for (BlockCodeToAmount blockCodeToAmount : blockCodeToAmounts) {
            blockCodes.add(blockCodeToAmount.getBlockCode());
        }
        return blockCodes;
    }

    public int getOwnedCount(String blockCode) {
        for (BlockCodeToAmount blockCodeToAmount : blockCodeToAmounts) {
            if (blockCodeToAmount.getBlockCode().equals(blockCode)) {
                return blockCodeToAmount.getAmount();
            }
        }
        throw new IllegalArgumentException("Block Code " + blockCode + " not valid fot card '" + name + "'.");
    }

    public void setDifference(int difference) {
        this.difference = difference;
    }

    public int getSelectedBlockIndex() {
        return selectedBlockIndex;
    }

    public void setSelectedBlockIndex(int selectedBlockIndex) {
        this.selectedBlockIndex = selectedBlockIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(BlockCodeToAmount bcta : blockCodeToAmounts){
            sb.append(bcta.getBlockCode())
              .append("=")
              .append(bcta.getAmount())
              .append(",");
        }
        return "EditableCard: " + name + ", " + sb.toString() + ", " + difference;
    }

    public BlockCodeToAmount getSelectedBlockAndAmount() {
        return blockCodeToAmounts.get(selectedBlockIndex);
    }

    @Override
    public int compareTo(@NonNull EditableCard other) {
        return this.name.compareTo(other.getName());
    }

    public void reverseDifference() {
        this.difference = -difference;
    }
}
