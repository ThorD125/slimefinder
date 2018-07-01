package slimefinder.core.search;

import slimefinder.util.Direction;
import slimefinder.util.Position;

public class SearchPath {
    /**
     * The current edge length
     */
    private int edge;

    /**
     * Number of steps taken on the current edge
     */
    private int steps;

    /**
     * Number of turns since the last edge length increase
     */
    private int turns;
    
    /**
     * These define the search area that this path spirally traverses. The
     * search area is a square of width, maxWidth, with a square of width, minWidth,
     * excluded from the middle.
     */
    private final int minWidth, maxWidth;
    
    private long progress;
    
    /**
     * The middle position of the spiral path. This is the starting position if 
     * not skipping any area in the middle (i.e. minWidth <= 0).
     */
    private final Position centerPos;
    
    /**
     * The direction of the next step
     */
    private Direction dir;

    private boolean inProgress;

    private Position position;

    public SearchPath(Position centerPos, int minWidth, int maxWidth) {
        this.maxWidth = Math.max(0, maxWidth);
        this.minWidth = Math.max(0, minWidth);
        this.centerPos = centerPos;
        position = Position.origin();
        init();
    }

    /**
     * calculates the starting position and prepares path for stepping
     */
    private void init() {
        progress = 0;
        steps = 0;
        int dx, dz;
        if (minWidth <= 0) {
            dir = Direction.EAST;
            dx = 0;
            dz = 0;
            edge = minWidth + 1;
            turns = 0;
        } else {
            if (minWidth % 2 == 0) {
                dir = Direction.NORTH;
                dx = -minWidth / 2;
                dz = minWidth / 2;
            } else {
                dir = Direction.SOUTH;
                dx = (minWidth + 1) / 2;
                dz = -(minWidth - 1) / 2;
            }
            edge = minWidth;
            turns = 1;
        }
        
        position.setPos(centerPos.x + dx, centerPos.z + dz);
        inProgress = false;
    }

    /**
     * Rotates the direction of the next step clockwise
     */
    private void turn() {
        if (dir == Direction.NORTH) {
            dir = Direction.EAST;
        } else if (dir == Direction.EAST) {
            dir = Direction.SOUTH;
        } else if (dir == Direction.SOUTH) {
            dir = Direction.WEST;
        } else if (dir == Direction.WEST) {
            dir = Direction.NORTH;
        }
        ++turns;
    }

    /**
     * Moves the position by 1 step along a spiral path around the starting
     * position. The first step initializes the position.
     * 
     * @return false when moving outside the search region, true otherwise.
     */
    public boolean step() {
        ++progress;
        if (!inProgress) {
            inProgress = true;
            return maxWidth > minWidth;
        }
        
        position.move(1, dir);
        ++steps;

        if (edge >= maxWidth && steps >= edge) {
            init();
        } else if (steps >= edge) {
            steps = 0;
            turn();
            if (turns > 1) {
                turns = 0;
                ++edge;
            }
        }
        return inProgress;
    }

    /**
     * @return current position on the path
     */
    public Position getPosition() {
        if (!inProgress) return null;
        return position;
    }
    
    /**
     * @return total number of steps taken on this path
     */
    public long getProgress() {
        return progress;
    }

    /**
     * @return total number of distinct positions on the path
     */
    public long getPathLength() {
        return Math.max(
            (long) maxWidth * (long) maxWidth - (long) minWidth * (long) minWidth,
            0
        );
    }
}
