package net.villenium.skywars.game.generators;

import net.villenium.skywars.utils.AlgoUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class ChestGenerator {
    private final Map<String, Integer> pUsages = new HashMap<>();

    private final Map<String, List<List<ItemStack>>> pLeft = new HashMap<>();

    private final int defUsages = 2;

    private final List<ItemStack> commonItems = getCommonItems();

    private final List<ItemStack> centerItems = getCenterItems();

    private final int minimum;

    private final int maximum;

    public ChestGenerator(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    protected abstract List<List<ItemStack>> getDefaultItems();

    protected abstract List<ItemStack> getCommonItems();

    protected abstract List<ItemStack> getCenterItems();

    public List<ItemStack> generateCommonItems(Player p) {
        return generate(this.commonItems, p);
    }

    public List<ItemStack> generateCenterItems(Player p) {
        return generate(this.commonItems, p);
    }

    public void clear() {
        this.pUsages.clear();
        this.pLeft.clear();
    }

    private List<ItemStack> generate(List<ItemStack> source, Player p) {
        Integer usages = this.pUsages.get(p.getName());
        if (usages == null)
            usages = Integer.valueOf(0);
        if (usages.intValue() < 2) {
            List<List<ItemStack>> left = this.pLeft.get(p.getName());
            if (left == null) {
                left = getDefaultItems();
                Collections.shuffle(left);
            }
            ArrayList<ItemStack> arrayList = new ArrayList();
            int j = left.size();
            if (usages.intValue() != 1 && j > 1)
                j = 1 + AlgoUtil.r(j >> 1);
            for (int i = 0; i < j; i++) {
                List<ItemStack> current = left.remove(left.size() - 1);
                arrayList.add(current.get(AlgoUtil.r(current.size())));
            }
            this.pUsages.put(p.getName(), Integer.valueOf(usages.intValue() + 1));
            this.pLeft.put(p.getName(), left);
            return arrayList;
        }
        int amount = AlgoUtil.r(this.maximum - this.minimum + 1) + this.minimum;
        ArrayList<ItemStack> result = new ArrayList();
        for (int size = 0; size < amount; size++) {
            ItemStack current = source.get(AlgoUtil.r(source.size()));
            boolean has;
            for (has = true; has; has = false) {
                Iterator<ItemStack> var9 = result.iterator();
                while (var9.hasNext()) {
                    ItemStack is = var9.next();
                    if (is.getType() == current.getType()) {
                        current = source.get(AlgoUtil.r(source.size()));
                        break;
                    }
                }
            }
            current = current.clone();
            int am = current.getAmount();
            if (am > 2) {
                am += AlgoUtil.r(am >> 1);
                am = Math.min(64, am);
                current.setAmount(am);
            }
            result.add(current);
        }
        return result;
    }

    protected void add(List<ItemStack> list, ItemStack item, int amount) {
        for (int i = 0; i < amount; i++)
            list.add(item);
    }
}
