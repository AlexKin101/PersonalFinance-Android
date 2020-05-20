package com.example.personalfinancedemo;

import android.graphics.Color;
import android.graphics.Point;

public class StatisticalItem {

    private float percent;
    private int color = Color.GREEN;
    private String topMarkText = "Top Mark";
    private String bottomMarkText = "Bottom Mark";
    private int markTextColor = 0;

    private Point dotPoint;
    private Point gapPoint;
    private Point lineEndPoint;
    private Point topMarkPoint;
    private Point bottomMarkPoint;

    private Point gapAnimationPoint;
    private Point lineEndAnimPoint;
    private Point topMarkAnimPoint;
    private Point bottomMarkAnimPoint;

    private float startAngle;
    private float sweepAngle;
    private float startAngleAnim;
    private float sweepAngleAnim;
    private float dotRadiusAnim;
    private float dotRadius;

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTopMarkText() {
        return topMarkText;
    }

    public void setTopMarkText(String topMarkText) {
        this.topMarkText = topMarkText;
    }

    public String getBottomMarkText() {
        return bottomMarkText;
    }

    public void setBottomMarkText(String bottomMarkText) {
        this.bottomMarkText = bottomMarkText;
    }

    public int getMarkTextColor() {
        return markTextColor;
    }

    public void setMarkTextColor(int markTextColor) {
        this.markTextColor = markTextColor;
    }


    public Point getDotPoint() {
        return dotPoint;
    }

    public void setDotPoint(Point dotPoint) {
        this.dotPoint = dotPoint;
    }

    public Point getGapPoint() {
        return gapPoint;
    }

    public void setGapPoint(Point gapPoint) {
        this.gapPoint = gapPoint;
    }

    public Point getLineEndPoint() {
        return lineEndPoint;
    }

    public void setLineEndPoint(Point lineEndPoint) {
        this.lineEndPoint = lineEndPoint;
    }

    public Point getTopMarkPoint() {
        return topMarkPoint;
    }

    public void setTopMarkPoint(Point topMarkPoint) {
        this.topMarkPoint = topMarkPoint;
    }

    public Point getBottomMarkPoint() {
        return bottomMarkPoint;
    }

    public void setBottomMarkPoint(Point bottomMarkPoint) {
        this.bottomMarkPoint = bottomMarkPoint;
    }

    public Point getGapAnimationPoint() {
        return gapAnimationPoint;
    }

    public void setGapAnimationPoint(Point gapAnimationPoint) {
        this.gapAnimationPoint = gapAnimationPoint;
    }

    public Point getLineEndAnimPoint() {
        return lineEndAnimPoint;
    }

    public void setLineEndAnimPoint(Point lineEndAnimPoint) {
        this.lineEndAnimPoint = lineEndAnimPoint;
    }

    public Point getTopMarkAnimPoint() {
        return topMarkAnimPoint;
    }

    public void setTopMarkAnimPoint(Point topMarkAnimPoint) {
        this.topMarkAnimPoint = topMarkAnimPoint;
    }

    public Point getBottomMarkAnimPoint() {
        return bottomMarkAnimPoint;
    }

    public void setBottomMarkAnimPoint(Point bottomMarkAnimPoint) {
        this.bottomMarkAnimPoint = bottomMarkAnimPoint;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public float getStartAngleAnim() {
        return startAngleAnim;
    }

    public void setStartAngleAnim(float startAngleAnim) {
        this.startAngleAnim = startAngleAnim;
    }

    public float getSweepAngleAnim() {
        return sweepAngleAnim;
    }

    public void setSweepAngleAnim(float sweepAngleAnim) {
        this.sweepAngleAnim = sweepAngleAnim;
    }

    public float getDotRadiusAnim() {
        return dotRadiusAnim;
    }

    public void setDotRadiusAnim(float dotRadiusAnim) {
        this.dotRadiusAnim = dotRadiusAnim;
    }

    public float getDotRadius() {
        return dotRadius;
    }

    public void setDotRadius(float dotRadius) {
        this.dotRadius = dotRadius;
    }
}
