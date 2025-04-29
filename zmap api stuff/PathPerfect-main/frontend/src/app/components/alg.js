/**
 * finds four paths (4 list of coords) based on source (src) and distance (dist)
 * @param {Array} src        the node A_1 where the runner starts
 * @param {Double} dist     how much the runner wants to run
 * @returns {Array<Array<Array>>}    4 possible paths for the runner to run
 */
export function theAlgorithm(src, dist) {
    let xAngle = Math.random();
    let yAngle = Math.sqrt(1- xAngle*xAngle);
    // dist/7 because it is makes the circle slightly smaller than r = dist/(2+pi);
    let xCoord = dist/7 * xAngle;
    let yCoord = dist/7 * yAngle;
    const numTurns = 4;

    return [findPath(src, dist, xCoord, yCoord, numTurns)]; 
}


/**
 * finds path based on source (src) and distance (dist) at a certain angle
 * @param {Array} src           the node A_i where the runner starts
 * @param {Double} dist         how much the runner wants to run
 * @param {Double} xCoord       xCoord of center of circle
 * @param {Double} yCoord       yCoord of center of circle
 * @param {Double} numTurns     num turns for path
 * @returns {Array<Array>}      4 possible paths for the runner to run
 */
function findPath(src, dist, xCoord, yCoord, numTurns) {

    let distLeft = dist;
    let path = [];
    let curr = src;
    console.log(totalDist(src[0], src[1], curr[0], curr[1]), distLeft);
    while (distLeft > 0 && totalDist(src[0], src[1], curr[0], curr[1]) < distLeft) {
        
        const tangentAngle = findTangentAngle(xCoord, yCoord, curr, numTurns);
        const varAngle = Math.PI/8;
        // will find three possible nextNodes
        let nextNodes = [];
        for (let i = 0; i <= 2; i++) {
            nextNodes.push(findNode(tangentAngle, i*varAngle, curr, dist/(numTurns*1.5)));
        }
        const nextNode = closestNode(nextNodes, curr, dist);
        const yNum = (nextNode[1]-curr[1]);
        const xNum = (nextNode[0]-curr[0]);
        const distSeg = Math.sqrt(yNum*yNum + xNum*xNum);
        distLeft -= distSeg;
        path.push(curr);
        curr = nextNode;
    }

    return path;

}

/**
 * QUESTIONS: find_closest_turn API
 * calculates best node A_{i+1} (next node) based on A_i (curr node)
 * @param {Double} angle    (radians) tangent angle for node A_i
 * @param {Double} varAngle (radians) variable angle relative to tangent angle from node A_i to node A_{i+1}
 * @param {Array} curr       the curr node A_i
 * @param {Double} dist     the ideal distance from A_i to A_{i+1}
 * @returns {Array}          the coordinate of A_{i+1}
 */
function findNode(angle, varAngle, curr, dist) {
    // Calculate new longitude and latitude
    const newLatitude = dist * Math.cos(angle+varAngle) + curr[0];
    const newLongitude = dist * Math.sin(angle+varAngle) + curr[1];

    // assume find_closest_turn API isn't needed
    // const turnCoord = find_closest_turn(newLatitude, newLongitude);
    const turnCoord = [newLatitude, newLongitude];
  
    return turnCoord;
}

/**
 * QUESTIONS: find_closest_distance API
 * finds the closest intersection based on coordinate
 * @param {Array<Array>} nodes list of possible nodes A_{i+1}
 * @param {Array} curr        the current node A_i
 * @param {Double} dist     the ideal distance from A_i to A_{i+1}
 * @returns {Array}           the best A_{i+1} that is closest to the ideal distance
 */
function closestNode(nodes, curr, dist) {
    if (nodes === undefined || nodes.length == 0) {
        return [];
    }

    const n = nodes.length;
    // assume that the API isn't needed
    // let minDist = Math.abs(find_closest_distance(curr, nodes[0])-dist);
    // let minNode = nodes[0];

    // for (let i = 1; i < n; i++) {
    //     const currDist = Math.abs(find_closest_distance(curr, nodes[i])-dist);
    //     if (currDist < minDist) {
    //         minDist = minDist;
    //         minNode = nodes[i];
    //     }
    // }

    let minNode = nodes[Math.floor((Math.random() * n))];

    return minNode;

}

/**
 * finds tangent angle for a point on the circle
 * ignore edge case of 179 -> -180, change if it is a problem
 * @param {Double} x        x coord for center of circle
 * @param {Double} y        y coord for center of circle
 * @param {Array} curr      coordinates for point on circle
 * @param {Double} numTurns num turns for path
 */
function findTangentAngle(x, y, curr, numTurns) {
    const dX = curr[0]-x;
    const dY = curr[1]-y;
    return Math.atan(dY/dX) + Math.PI/(numTurns/2);
}

function totalDist(lat1, lon1, lat2, lon2) {
    const dX = lat1-lat2;
    const dY = lon1-lon2;

    return Math.sqrt(dX*dX + dY*dY);
}

