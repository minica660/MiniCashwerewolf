package MiniCash.miniCashwerewolf.model;

import MiniCash.miniCashwerewolf.RoleManager;

public class Role {

    private RoleManager.RoleType roleType;
    private boolean active;
    private int total;

    public Role(RoleManager.RoleType roleType, boolean active, int total) {
        this.roleType = roleType;
        this.active = active;
        this.total = total;
    }

    public RoleManager.RoleType getRoleType() {
        return roleType;
    }
    public boolean isActive() {
        return active;
    }

    public int getTotal(){
        return total;
    }

}
