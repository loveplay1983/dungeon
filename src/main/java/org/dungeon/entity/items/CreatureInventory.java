/*
 * Copyright (C) 2014 Bernardo Sulzbach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dungeon.entity.items;

import org.dungeon.entity.Weight;
import org.dungeon.entity.creatures.Creature;
import org.dungeon.io.DLogger;

/**
 * Inventory class that defines a common general-purpose Item storage and query structure.
 * <p/>
 * Change log Created by Bernardo on 19/09/2014.
 */
public class CreatureInventory extends BaseInventory implements LimitedInventory {

  private final Creature owner;
  private final int itemLimit;
  private final Weight weightLimit;

  public CreatureInventory(Creature owner, int itemLimit, double weightLimit) {
    this.owner = owner;
    this.itemLimit = itemLimit;
    this.weightLimit = Weight.newInstance(weightLimit);
  }

  @Override
  public int getItemLimit() {
    return itemLimit;
  }

  @Override
  public Weight getWeightLimit() {
    return weightLimit;
  }

  public Weight getWeight() {
    Weight sum = Weight.ZERO;
    for (Item item : getItems()) {
      sum = sum.add(item.getWeight());
    }
    return sum;
  }

  /**
   * Attempts to add an item object to this Inventory.
   *
   * @param item the Item to be added
   * @return true if successful, false otherwise
   */
  public AdditionResult addItem(Item item) {
    if (hasItem(item)) { // Check that the new item is not already in the inventory.
      DLogger.warning("Tried to add an item to a CreatureInventory that already has it.");
      return AdditionResult.ALREADY_IN_THE_INVENTORY;
    }
    if (isFull()) {
      return AdditionResult.AMOUNT_LIMIT;
    } else if (willExceedWeightLimitAfterAdding(item)) {
      return AdditionResult.WEIGHT_LIMIT;
    } else {
      items.add(item);
      item.setInventory(this);
      return AdditionResult.SUCCESSFUL;
    }
  }

  private boolean isFull() {
    return getItemCount() == getItemLimit();
  }

  private boolean willExceedWeightLimitAfterAdding(Item item) {
    return getWeight().add(item.getWeight()).compareTo(getWeightLimit()) > 0;
  }

  /**
   * Removes an Item from the CreatureInventory, unequipping it if it is the currently equipped weapon.
   *
   * @param item the Item to be removed from the CreatureInventory
   */
  public void removeItem(Item item) {
    if (owner.getWeapon() == item) {
      owner.unsetWeapon();
    }
    items.remove(item);
    item.setInventory(null);
  }

  public enum AdditionResult {ALREADY_IN_THE_INVENTORY, AMOUNT_LIMIT, WEIGHT_LIMIT, SUCCESSFUL}

}
