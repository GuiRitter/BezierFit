# README #

![graphical user interface](image/GUI.gif)

This program was hastly created because I needed the Bézier curve that described a curve present in an edge map in order to develop another program. As such, it's not very polished, and missing several features, but works pretty well.

It works by reading an initial guess, which must be supplied by the user. I made no attempts at generating this initial guess, because I needed this program finished quickly. You can use my [Bézier Drawer](https://github.com/GuiRitter/BezierDrawer) to make that initial guess. Then the control points are moved to a random distance and angle, a metric is calculated based on the curve to be fitted and, if this metric is better than the previous one, the control points' locations are replaced by the newly created ones. This is repeated forever.

You have the following features:
* Set the display zoom
* Set the maximum distance to vary the control points' locations
    * It will choose a random value between zero and the provided one
    * Affects the algorithm on the fly
* Add a control point before the selected one or at the end if no points are selected
* Remove the selected control point
* Set the curve's density
    * Same as on the Bézier Drawer, how many points are drawn in the curve. Since this affects how many points the fitted curve will have, it affects the fitting process
* See the control points' locations and metric value of the last fitted curve

Requires my [ImageComponent](https://github.com/GuiRitter/ImageComponent) library.

[A few words about Maven.](https://gist.github.com/GuiRitter/1834bd024756e08ab422026a7cd24605)
