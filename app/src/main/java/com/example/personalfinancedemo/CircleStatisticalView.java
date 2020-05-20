package com.example.personalfinancedemo;

import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

    /**
     * Created by Relin
     * on 2018-08-14.
     * 圆形统计图
     */

public class CircleStatisticalView extends View {

    /**
     * 宽度
     */
    private float width;
    /**
     * 高度
     */
    private float height;
    /**
     * 中心坐标
     */
    private float circleX, circleY;
    /**
     * 半径
     */
    private float circleRadius = dpToPx(50);
    /**
     * 圆圈背景颜色
     */
    private int circleBackgroundColor = Color.parseColor("#EDEDED");
    /**
     * 圆圈宽度
     */
    private float circleStrokeWidth = dpToPx(30);

    /**
     * 圆点边距
     */
    private float dotMargin = dpToPx(5);
    /**
     * 圆点半径
     */
    private float dotRadius = dpToPx(5);
    /**
     * 转角线X差值
     */
    private float lineGapX = dpToPx(15);
    /**
     * 转角线Y差值
     */
    private float lineGapY = dpToPx(15);
    /**
     * 标记线上下的文件间距
     */
    private float lineNearTextMargin = dpToPx(5);
    /**
     * 线条粗细
     */
    private float lineStrokeWidth = dpToPx(1.2F);
    /**
     * 标记文字大小
     */
    private float markTextSize = dpToPx(14);
    /**
     * 标记文字颜色
     */
    private int markTextColor = 0;

    /**
     * 数据
     */
    private List<StatisticalItem> statisticalItems;
    /**
     * 是否使用动画
     */
    private boolean isUseAnimation = true;

    /**
     * 动画持续时间
     */
    private int animationDuration = 500;


    public CircleStatisticalView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleStatisticalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleStatisticalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int w = widthSpecSize;
        int h = heightSpecSize;
        int needHeight = (int) (circleRadius * 2 + dotMargin + dotRadius * 2 + circleStrokeWidth * 2 + lineGapY * 4);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            w = (int) (1.7F * needHeight);
            h = needHeight;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            w = 4 * needHeight / 5;
            h = heightSpecSize;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            w = widthSpecSize;
            h = needHeight;
        }
        setMeasuredDimension(w, h);
        //获取最终的宽高
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        circleX = width / 2;
        circleY = height / 2;
        circleRadius -= getPaddingLeft() - getPaddingRight();
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleStatisticalView);
        circleBackgroundColor = array.getColor(R.styleable.CircleStatisticalView_circleBackgroundColor, circleBackgroundColor);
        dotMargin = array.getDimension(R.styleable.CircleStatisticalView_dotMargin, dotMargin);
        dotRadius = array.getDimension(R.styleable.CircleStatisticalView_dotRadius, dotRadius);
        lineGapX = array.getDimension(R.styleable.CircleStatisticalView_lineGapX, lineGapX);
        lineGapY = array.getDimension(R.styleable.CircleStatisticalView_lineGapY, lineGapY);
        lineNearTextMargin = array.getDimension(R.styleable.CircleStatisticalView_lineNearTextMargin, lineNearTextMargin);
        lineStrokeWidth = array.getDimension(R.styleable.CircleStatisticalView_lineStrokeWidth, lineStrokeWidth);
        markTextSize = array.getDimension(R.styleable.CircleStatisticalView_markTextSize, markTextSize);
        markTextColor = array.getColor(R.styleable.CircleStatisticalView_markTextColor, markTextColor);
        isUseAnimation = array.getBoolean(R.styleable.CircleStatisticalView_isUseAnimation, isUseAnimation);
        animationDuration = array.getInt(R.styleable.CircleStatisticalView_isUseAnimation, animationDuration);
        statisticalItems = new ArrayList<>();
        array.recycle();
    }

    private int drawCount = 0;

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        int size = statisticalItems == null ? 0 : statisticalItems.size();
        float startAngle = 0F;
        float markAngle;
        for (int i = 0; i < size; i++) {
            StatisticalItem item = statisticalItems.get(i);
            float sweepAngle = item.getPercent() * 360F;
            statisticalItems.get(i).setStartAngle(startAngle);
            statisticalItems.get(i).setSweepAngle(sweepAngle);
            if (!isUseAnimation) {
                drawArc(canvas, startAngle - 1F, sweepAngle + 1F, item.getColor());
            }
            if (i == 0) {
                markAngle = sweepAngle;
            } else {
                markAngle = 2 * (startAngle + sweepAngle / 2);
            }
            drawMark(canvas, i, item, markAngle);
            startAngle += sweepAngle;
        }
        if (isUseAnimation) {
            userAnimationDraw(canvas, size, drawCount);
        }
        drawCount++;
    }

    /**
     * 利用动画绘制
     *
     * @param canvas    画布
     * @param size      数据大小
     * @param drawCount 绘制次数
     */
    private void userAnimationDraw(Canvas canvas, int size, int drawCount) {
        //动画绘制
        for (int i = 0; i < size; i++) {
            StatisticalItem item = statisticalItems.get(i);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(item.getColor());
            //圆饼
            if (item.getSweepAngleAnim() != 0) {
                drawArc(canvas, item.getStartAngle() - 1F, item.getSweepAngleAnim() + 1F, item.getColor());
            }
            //圆点
            if (item.getDotRadiusAnim() != 0) {
                canvas.drawCircle(item.getDotPoint().x, item.getDotPoint().y, item.getDotRadiusAnim(), paint);
            }
            //标记线和标记
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dpToPx(1));
            if (item.getGapAnimationPoint() != null) {
                canvas.drawLine(item.getDotPoint().x, item.getDotPoint().y, item.getGapAnimationPoint().x, item.getGapAnimationPoint().y, paint);
            }
            if (item.getLineEndAnimPoint() != null) {
                canvas.drawLine(item.getGapPoint().x, item.getGapPoint().y, item.getLineEndAnimPoint().x, item.getLineEndAnimPoint().y, paint);
            }
            //文字
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(dpToPx(16));
            paint.setColor(item.getColor());
            if (markTextColor != 0) {
                paint.setColor(markTextColor);
            }
            if (item.getMarkTextColor() != 0) {
                paint.setColor(item.getMarkTextColor());
            }
            if (item.getTopMarkAnimPoint() != null) {
                paint.setTextSize(markTextSize);
                canvas.drawText(item.getTopMarkText(), item.getTopMarkAnimPoint().x, item.getTopMarkAnimPoint().y, paint);
            }
            if (item.getBottomMarkAnimPoint() != null) {
                paint.setTextSize(markTextSize);
                canvas.drawText(item.getBottomMarkText(), item.getBottomMarkAnimPoint().x, item.getBottomMarkAnimPoint().y, paint);
            }
        }
        //开启动画
        if (drawCount == 0) {
            for (int i = 0; i < size; i++) {
                final StatisticalItem item = statisticalItems.get(i);
                ValueAnimator angleAnim = drawArcAnimation(i, item.getStartAngle(), item.getSweepAngle());
                ValueAnimator dotAnim = drawDotAnimation(i, 0, item.getDotRadius());
                ValueAnimator lineStartAnim = drawMarkLineAnimation(i, item.getDotPoint(), item.getGapPoint(), 1);
                ValueAnimator lineEndAnim = drawMarkLineAnimation(i, item.getGapPoint(), item.getLineEndPoint(), 2);
                ValueAnimator topMarkAnim = drawMarkLineAnimation(i, item.getGapPoint(), item.getTopMarkPoint(), 3);
                Point point = new Point();
                point.x = item.getBottomMarkPoint().x;
                point.y = (int) (item.getBottomMarkPoint().y + dpToPx(30));
                ValueAnimator bottomMarkAnim = drawMarkLineAnimation(i, point, item.getBottomMarkPoint(), 4);
                //顶部文字延迟500ms
                topMarkAnim.setStartDelay(animationDuration);
                //动画集
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(dotAnim).after(angleAnim);
                animatorSet.play(lineStartAnim).after(angleAnim);
                animatorSet.play(lineEndAnim).after(lineStartAnim);
                animatorSet.play(topMarkAnim).after(lineStartAnim);
                animatorSet.play(bottomMarkAnim).after(topMarkAnim);
                animatorSet.start();
            }
        }
    }


    /**
     * 画饼动画
     *
     * @param position   位置
     * @param startAngle 开始弧度
     * @param sweepAngle 结束弧度
     * @return
     */
    public ValueAnimator drawArcAnimation(final int position, final float startAngle, final float sweepAngle) {
        final ValueAnimator valueAnimator = ValueAnimator.ofObject(new TypeEvaluator<Float>() {
            @Override
            public Float evaluate(float fraction, Float startValue, Float endValue) {
                return fraction * endValue;
            }
        }, startAngle, sweepAngle);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                statisticalItems.get(position).setSweepAngleAnim((Float) animation.getAnimatedValue());
                invalidate();
            }
        });
        valueAnimator.setDuration(animationDuration);
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }

    /**
     * 圆点动画
     *
     * @param position    位置
     * @param startRadius 开始半径
     * @param endRadius   结束半径
     * @return
     */
    public ValueAnimator drawDotAnimation(final int position, final float startRadius, final float endRadius) {
        final ValueAnimator valueAnimator = ValueAnimator.ofObject(new TypeEvaluator<Float>() {
            @Override
            public Float evaluate(float fraction, Float startValue, Float endValue) {
                return fraction * endValue;
            }
        }, startRadius, endRadius);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                statisticalItems.get(position).setDotRadiusAnim((Float) animation.getAnimatedValue());
                invalidate();
            }
        });
        valueAnimator.setDuration(animationDuration);
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }

    /**
     * 绘制标记和线的动画
     *
     * @param position   位置
     * @param startPoint 开始点
     * @param endPoint   结束点
     * @param step       步骤
     * @return
     */
    public ValueAnimator drawMarkLineAnimation(final int position, final Point startPoint, final Point endPoint, final int step) {
        final ValueAnimator valueAnimator = ValueAnimator.ofObject(new TypeEvaluator<Point>() {
            @Override
            public Point evaluate(float fraction, Point startValue, Point endValue) {
                Point point = new Point();
                point.x = (int) (fraction * (endPoint.x - startPoint.x) + startPoint.x);
                point.y = (int) (fraction * (endValue.y - startValue.y) + startPoint.y);
                return point;
            }
        }, startPoint, endPoint);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                switch (step) {
                    case 1:
                        valueAnimator.setDuration(animationDuration / 2);
                        statisticalItems.get(position).setGapAnimationPoint((Point) animation.getAnimatedValue());
                        break;
                    case 2:
                        valueAnimator.setDuration(animationDuration);
                        statisticalItems.get(position).setLineEndAnimPoint((Point) animation.getAnimatedValue());
                        break;
                    case 3:
                        valueAnimator.setDuration(animationDuration);
                        statisticalItems.get(position).setTopMarkAnimPoint((Point) animation.getAnimatedValue());
                        break;
                    case 4:
                        valueAnimator.setDuration(animationDuration);
                        statisticalItems.get(position).setBottomMarkAnimPoint((Point) animation.getAnimatedValue());
                        break;
                }
                invalidate();
            }
        });
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }


    /**
     * 创建画笔对象
     *
     * @return
     */
    private Paint buildPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(circleStrokeWidth);
        return paint;
    }

    /**
     * 画出背景圆圈
     *
     * @param canvas 画布
     */
    private void drawCircle(Canvas canvas) {
        Paint paint = buildPaint();
        paint.setColor(circleBackgroundColor);
        canvas.drawCircle(circleX, circleY, circleRadius, paint);
    }


    /**
     * 画圆弧
     *
     * @param canvas     画布
     * @param startAngle 开始弧度
     * @param sweepAngle 进过的弧度
     * @param color      弧面颜色
     */
    private void drawArc(Canvas canvas, float startAngle, float sweepAngle, int color) {
        Paint paint = buildPaint();
        paint.setColor(color);
        RectF rectF = new RectF(circleX - circleRadius, circleY - circleRadius, circleX + circleRadius, circleY + circleRadius);
        canvas.drawArc(rectF, -90 + startAngle, sweepAngle, false, paint);
    }

    /**
     * 绘制标记
     *
     * @param canvas    画布
     * @param item      标记数据对象
     * @param markAngle 标记弧度
     */
    private void drawMark(Canvas canvas, int position, StatisticalItem item, float markAngle) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineStrokeWidth);
        paint.setColor(item.getColor());
        //圆点
        float dotToCenterLine = circleRadius + dpToPx(20) + dotMargin;
        float dotX;
        float dotY;
        float dotAngle = (float) ((markAngle / 2 * Math.PI / 180));//角度转弧度
        if (markAngle / 2 < 180 * 2) {
            dotX = (float) (circleX + Math.sin(dotAngle) * dotToCenterLine);
            dotY = (float) (circleY - Math.cos(dotAngle) * dotToCenterLine);
        } else {
            dotX = (float) (circleX - Math.sin(dotAngle) * dotToCenterLine);
            dotY = (float) (circleY + Math.cos(dotAngle) * dotToCenterLine);
        }
        if (!isUseAnimation) {
            canvas.drawCircle(dotX, dotY, dotRadius, paint);
        }
        statisticalItems.get(position).setDotRadius(dotRadius);
        //线
        float lineMiddleX, lineMiddleY, lineEdnX, lineEndY;
        if (circleX < dotX) {
            lineMiddleX = markAngle / 2 > 45 && markAngle / 2 < 135 ? dotX : dotX + lineGapX;
            lineMiddleY = markAngle / 2 > 45 && markAngle / 2 < 135 ? dotY : dotY + (circleY < dotY ? +lineGapY : -lineGapY);
            lineEdnX = markAngle / 2 > 45 && markAngle / 2 < 135 ? lineMiddleX + (width - lineMiddleX - getPaddingRight()) : lineMiddleX + (width - lineMiddleX - getPaddingRight());
            lineEndY = lineMiddleY;
        } else {
            lineMiddleX = markAngle / 2 > 225 && markAngle / 2 < 315 ? dotX : dotX - lineGapX;
            lineMiddleY = markAngle / 2 > 225 && markAngle / 2 < 315 ? dotY : dotY + (circleY < dotY ? +lineGapY : -lineGapY);
            lineEdnX = 0 + getPaddingLeft();
            lineEndY = lineMiddleY;
        }
        if (!isUseAnimation) {
            canvas.drawLine(dotX, dotY, lineMiddleX, lineMiddleY, paint);
            canvas.drawLine(lineMiddleX, lineMiddleY, lineEdnX, lineEndY, paint);
        }
        statisticalItems.get(position).setDotPoint(new Point((int) dotX, (int) dotY));
        statisticalItems.get(position).setGapPoint(new Point((int) lineMiddleX, (int) lineMiddleY));
        statisticalItems.get(position).setLineEndPoint(new Point((int) lineEdnX, (int) lineEndY));

        //文字
        paint.setColor(item.getColor());
        if (markTextColor != 0) {
            paint.setColor(markTextColor);
        }
        if (item.getMarkTextColor() != 0) {
            paint.setColor(item.getMarkTextColor());
        }
        paint.setTextSize(markTextSize);
        String topMark = item.getTopMarkText();
        String bottomMark = item.getBottomMarkText();
        float topX, topY, bottomX, bottomY;
        if (circleX < dotX) {
            //上部分文字
            topX = lineEdnX - paint.measureText(topMark);
            topY = lineEndY - lineNearTextMargin;
            //下部分文字
            bottomX = lineEdnX - paint.measureText(bottomMark);
            bottomY = lineEndY + (paint.measureText(bottomMark) / bottomMark.length()) / 2 + lineNearTextMargin * 1.5F + lineStrokeWidth;
        } else {
            //上部分文字
            topX = lineEdnX;
            topY = lineEndY - lineNearTextMargin;
            //下部分文字
            bottomX = lineEdnX;
            bottomY = lineEndY + (paint.measureText(bottomMark) / bottomMark.length()) / 2 + lineNearTextMargin * 1.5F + lineStrokeWidth;
        }
        if (!isUseAnimation) {
            canvas.drawText(topMark, topX, topY, paint);
            canvas.drawText(bottomMark, bottomX, bottomY, paint);
        }
        statisticalItems.get(position).setTopMarkPoint(new Point((int) topX, (int) topY));
        statisticalItems.get(position).setBottomMarkPoint(new Point((int) bottomX, (int) bottomY));
    }


    private static float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }


    private static float dpToPx(float dp) {
        return dp * getScreenDensity();
    }


    public float getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(float circleRadius) {
        this.circleRadius = dpToPx(circleRadius);
        postInvalidate();
    }

    public int getCircleBackgroundColor() {
        return circleBackgroundColor;
    }

    public void setCircleBackgroundColor(int circleBackgroundColor) {
        this.circleBackgroundColor = circleBackgroundColor;
        postInvalidate();
    }

    public float getCircleStrokeWidth() {
        return circleStrokeWidth;
    }

    public void setCircleStrokeWidth(float circleStrokeWidth) {
        this.circleStrokeWidth = dpToPx(circleStrokeWidth);
        postInvalidate();
    }

    public float getDotMargin() {
        return dotMargin;
    }

    public void setDotMargin(float dotMargin) {
        this.dotMargin = dpToPx(dotMargin);
        postInvalidate();
    }

    public float getDotRadius() {
        return dotRadius;
    }

    public void setDotRadius(float dotRadius) {
        this.dotRadius = dpToPx(dotRadius);
        postInvalidate();
    }

    public float getLineGapX() {
        return lineGapX;
    }

    public void setLineGapX(float lineGapX) {
        this.lineGapX = dpToPx(lineGapX);
        postInvalidate();
    }

    public float getLineGapY() {
        return lineGapY;
    }

    public void setLineGapY(float lineGapY) {
        this.lineGapY = dpToPx(lineGapY);
        postInvalidate();
    }

    public float getLineNearTextMargin() {
        return lineNearTextMargin;
    }

    public void setLineNearTextMargin(float lineNearTextMargin) {
        this.lineNearTextMargin = dpToPx(lineNearTextMargin);
        postInvalidate();
    }

    public float getLineStrokeWidth() {
        return lineStrokeWidth;
    }

    public void setLineStrokeWidth(float lineStrokeWidth) {
        this.lineStrokeWidth = dpToPx(lineStrokeWidth);
        postInvalidate();
    }

    public float getMarkTextSize() {
        return markTextSize;
    }

    public void setMarkTextSize(float markTextSize) {
        this.markTextSize = dpToPx(markTextSize);
        postInvalidate();
    }

    public int getMarkTextColor() {
        return markTextColor;
    }

    public void setMarkTextColor(int markTextColor) {
        this.markTextColor = markTextColor;
        postInvalidate();
    }

    public List<StatisticalItem> getStatisticalItems() {
        return statisticalItems;
    }

    public void setStatisticalItems(List<StatisticalItem> statisticalItems) {
        int size = statisticalItems == null ? 0 : statisticalItems.size();
        float totalPercent = 0;
        List<StatisticalItem> newItems = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (statisticalItems.get(i).getPercent() != 0) {
                newItems.add(statisticalItems.get(i));
            }
        }
        size = newItems == null ? 0 : newItems.size();
        for (int i = 0; i < size; i++) {
            StatisticalItem item = newItems.get(i);
            totalPercent += item.getPercent();
        }
        if (totalPercent != 1) {
            for (int i = 0; i < size; i++) {
                StatisticalItem item = newItems.get(i);
                newItems.get(i).setPercent(item.getPercent() / totalPercent);
            }
        }
        this.statisticalItems = newItems;
        postInvalidate();
    }

    public boolean isUseAnimation() {
        return isUseAnimation;
    }

    public void setUseAnimation(boolean useAnimation) {
        isUseAnimation = useAnimation;
        postInvalidate();
    }
}