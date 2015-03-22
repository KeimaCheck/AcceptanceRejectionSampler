# AcceptanceRejectionSampler
Implentation of acceptance-rejection sampling algorithm for producing samples from arbitrary finitely-supported
probability distributions.

For a general description of the algorithm, check http://en.wikipedia.org/wiki/Rejection_sampling

This code works by preprocessing a given distribution to obtain what we refer to as a "boxed envelope"
of its density function. The boxed envelope is simply a step (piecewise-constant) function which
always takes values greater than or equal to the values of the original density function. A
distribution with such a density can be sampled from uniformly in time logarithmic with respect
to the number of subintervals of the domain. Then we use the rejection method to "suppress" this
sample to a uniform sample of the original distribution.

There is currently no implemented "fallback" method for sampling from the infinite tail of a
distribution with infinite support.

To facilitate logarithmic time sampling of the boxed envelope, we represent it internally as
a binary search tree whose nodes are pairs of two intervals (an interval along the x-axis and
a subinterval of the probability codomain (0,1)) and the value of the envelope on that interval.
This tree is not guaranteed to be balanced when first computed, but it can be serialized and
reconstructed and the reconstruction is always guaranteed to be balanced (behavior not yet implemented).

Most of the logic was hacked together over one weekend, so please don't take this as
a definitive glimpse at my programming ability.

Nicholas J. Padinha, 3/22/2015
(Version 0.1)
