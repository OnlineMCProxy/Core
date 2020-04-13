package com.github.derrop.proxy.scoreboard.minecraft;

import com.github.derrop.proxy.scoreboard.minecraft.criteria.IScoreObjectiveCriteria;

public class ScoreObjective
{
    private final Scoreboard theScoreboard;
    private final String name;

    private int displaySlot;

    /** The ScoreObjectiveCriteria for this objetive */
    private final IScoreObjectiveCriteria objectiveCriteria;
    private IScoreObjectiveCriteria.EnumRenderType renderType;
    private String displayName;

    public ScoreObjective(Scoreboard theScoreboardIn, String nameIn, IScoreObjectiveCriteria objectiveCriteriaIn)
    {
        this.theScoreboard = theScoreboardIn;
        this.name = nameIn;
        this.objectiveCriteria = objectiveCriteriaIn;
        this.displayName = nameIn;
        this.renderType = objectiveCriteriaIn.getRenderType();
    }

    public Scoreboard getScoreboard()
    {
        return this.theScoreboard;
    }

    public String getName()
    {
        return this.name;
    }

    public IScoreObjectiveCriteria getCriteria()
    {
        return this.objectiveCriteria;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public void setDisplaySlot(int displaySlot) {
        this.displaySlot = displaySlot;
    }

    public int getDisplaySlot() {
        return displaySlot;
    }

    public void setDisplayName(String nameIn)
    {
        this.displayName = nameIn;
        this.theScoreboard.func_96532_b(this);
    }

    public IScoreObjectiveCriteria.EnumRenderType getRenderType()
    {
        return this.renderType;
    }

    public void setRenderType(IScoreObjectiveCriteria.EnumRenderType type)
    {
        this.renderType = type;
        this.theScoreboard.func_96532_b(this);
    }
}