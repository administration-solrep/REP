/*!
 * jQuery corner plugin: simple corner rounding
 * Examples and documentation at: http://jquery.malsup.com/corner/
 * version 2.12 (23-MAY-2011)
 * Requires jQuery v1.3.2 or later
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 * Authors: Dave Methvin and Mike Alsup
 */

/**
 *  corner() takes a single string argument:  jQuery('#myDiv').corner("effect corners width")
 *
 *  effect:  name of the effect to apply, such as round, bevel, notch, bite, etc (default is round). 
 *  corners: one or more of: top, bottom, tr, tl, br, or bl.  (default is all corners)
 *  width:   width of the effect; in the case of rounded corners this is the radius. 
 *           specify this value using the px suffix such as 10px (yes, it must be pixels).
 */
;(function(jQuery) { 

var style = document.createElement('div').style,
    moz = style['MozBorderRadius'] !== undefined,
    webkit = style['WebkitBorderRadius'] !== undefined,
    radius = style['borderRadius'] !== undefined || style['BorderRadius'] !== undefined,
    mode = document.documentMode || 0,
    noBottomFold = jQuery.browser.msie && ((jQuery.browser.version < 8 && !mode) || mode < 8),

    expr = jQuery.browser.msie && (function() {
        var div = document.createElement('div');
        try { div.style.setExpression('width','0+0'); div.style.removeExpression('width'); }
        catch(e) { return false; }
        return true;
    })();

jQuery.support = jQuery.support || {};
jQuery.support.borderRadius = moz || webkit || radius; // so you can do:  if (!jQuery.support.borderRadius) jQuery('#myDiv').corner();

function sz(el, p) { 
    return parseInt(jQuery.css(el,p))||0; 
};
function hex2(s) {
    s = parseInt(s).toString(16);
    return ( s.length < 2 ) ? '0'+s : s;
};
function gpc(node) {
    while(node) {
        var v = jQuery.css(node,'backgroundColor'), rgb;
        if (v && v != 'transparent' && v != 'rgba(0, 0, 0, 0)') {
            if (v.indexOf('rgb') >= 0) { 
                rgb = v.match(/\d+/g); 
                return '#'+ hex2(rgb[0]) + hex2(rgb[1]) + hex2(rgb[2]);
            }
            return v;
        }
        if (node.nodeName.toLowerCase() == 'html')
            break;
        node = node.parentNode; // keep walking if transparent
    }
    return '#ffffff';
};

function getWidth(fx, i, width) {
    switch(fx) {
    case 'round':  return Math.round(width*(1-Math.cos(Math.asin(i/width))));
    case 'cool':   return Math.round(width*(1+Math.cos(Math.asin(i/width))));
    case 'sharp':  return width-i;
    case 'bite':   return Math.round(width*(Math.cos(Math.asin((width-i-1)/width))));
    case 'slide':  return Math.round(width*(Math.atan2(i,width/i)));
    case 'jut':    return Math.round(width*(Math.atan2(width,(width-i-1))));
    case 'curl':   return Math.round(width*(Math.atan(i)));
    case 'tear':   return Math.round(width*(Math.cos(i)));
    case 'wicked': return Math.round(width*(Math.tan(i)));
    case 'long':   return Math.round(width*(Math.sqrt(i)));
    case 'sculpt': return Math.round(width*(Math.log((width-i-1),width)));
    case 'dogfold':
    case 'dog':    return (i&1) ? (i+1) : width;
    case 'dog2':   return (i&2) ? (i+1) : width;
    case 'dog3':   return (i&3) ? (i+1) : width;
    case 'fray':   return (i%2)*width;
    case 'notch':  return width; 
    case 'bevelfold':
    case 'bevel':  return i+1;
    case 'steep':  return i/2 + 1;
    case 'invsteep':return (width-i)/2+1;
    }
};

jQuery.fn.corner = function(options) {
    // in 1.3+ we can fix mistakes with the ready state
    if (this.length == 0) {
        if (!jQuery.isReady && this.selector) {
            var s = this.selector, c = this.context;
            jQuery(function() {
                jQuery(s,c).corner(options);
            });
        }
        return this;
    }

    return this.each(function(index){
        var jQuerythis = jQuery(this),
            // meta values override options
            o = [jQuerythis.attr(jQuery.fn.corner.defaults.metaAttr) || '', options || ''].join(' ').toLowerCase(),
            keep = /keep/.test(o),                       // keep borders?
            cc = ((o.match(/cc:(#[0-9a-f]+)/)||[])[1]),  // corner color
            sc = ((o.match(/sc:(#[0-9a-f]+)/)||[])[1]),  // strip color
            width = parseInt((o.match(/(\d+)px/)||[])[1]) || 10, // corner width
            re = /round|bevelfold|bevel|notch|bite|cool|sharp|slide|jut|curl|tear|fray|wicked|sculpt|long|dog3|dog2|dogfold|dog|invsteep|steep/,
            fx = ((o.match(re)||['round'])[0]),
            fold = /dogfold|bevelfold/.test(o),
            edges = { T:0, B:1 },
            opts = {
                TL:  /top|tl|left/.test(o),       TR:  /top|tr|right/.test(o),
                BL:  /bottom|bl|left/.test(o),    BR:  /bottom|br|right/.test(o)
            },
            // vars used in func later
            strip, pad, cssHeight, j, bot, d, ds, bw, i, w, e, c, common, jQueryhorz;
        
        if ( !opts.TL && !opts.TR && !opts.BL && !opts.BR )
            opts = { TL:1, TR:1, BL:1, BR:1 };
            
        // support native rounding
        if (jQuery.fn.corner.defaults.useNative && fx == 'round' && (radius || moz || webkit) && !cc && !sc) {
            if (opts.TL)
                jQuerythis.css(radius ? 'border-top-left-radius' : moz ? '-moz-border-radius-topleft' : '-webkit-border-top-left-radius', width + 'px');
            if (opts.TR)
                jQuerythis.css(radius ? 'border-top-right-radius' : moz ? '-moz-border-radius-topright' : '-webkit-border-top-right-radius', width + 'px');
            if (opts.BL)
                jQuerythis.css(radius ? 'border-bottom-left-radius' : moz ? '-moz-border-radius-bottomleft' : '-webkit-border-bottom-left-radius', width + 'px');
            if (opts.BR)
                jQuerythis.css(radius ? 'border-bottom-right-radius' : moz ? '-moz-border-radius-bottomright' : '-webkit-border-bottom-right-radius', width + 'px');
            return;
        }
            
        strip = document.createElement('div');
        jQuery(strip).css({
            overflow: 'hidden',
            height: '1px',
            minHeight: '1px',
            fontSize: '1px',
            backgroundColor: sc || 'transparent',
            borderStyle: 'solid'
        });
    
        pad = {
            T: parseInt(jQuery.css(this,'paddingTop'))||0,     R: parseInt(jQuery.css(this,'paddingRight'))||0,
            B: parseInt(jQuery.css(this,'paddingBottom'))||0,  L: parseInt(jQuery.css(this,'paddingLeft'))||0
        };

        if (typeof this.style.zoom != undefined) this.style.zoom = 1; // force 'hasLayout' in IE
        if (!keep) this.style.border = 'none';
        strip.style.borderColor = cc || gpc(this.parentNode);
        cssHeight = jQuery(this).outerHeight();

        for (j in edges) {
            bot = edges[j];
            // only add stips if needed
            if ((bot && (opts.BL || opts.BR)) || (!bot && (opts.TL || opts.TR))) {
                strip.style.borderStyle = 'none '+(opts[j+'R']?'solid':'none')+' none '+(opts[j+'L']?'solid':'none');
                d = document.createElement('div');
                jQuery(d).addClass('jquery-corner');
                ds = d.style;

                bot ? this.appendChild(d) : this.insertBefore(d, this.firstChild);

                if (bot && cssHeight != 'auto') {
                    if (jQuery.css(this,'position') == 'static')
                        this.style.position = 'relative';
                    ds.position = 'absolute';
                    ds.bottom = ds.left = ds.padding = ds.margin = '0';
                    if (expr)
                        ds.setExpression('width', 'this.parentNode.offsetWidth');
                    else
                        ds.width = '100%';
                }
                else if (!bot && jQuery.browser.msie) {
                    if (jQuery.css(this,'position') == 'static')
                        this.style.position = 'relative';
                    ds.position = 'absolute';
                    ds.top = ds.left = ds.right = ds.padding = ds.margin = '0';
                    
                    // fix ie6 problem when blocked element has a border width
                    if (expr) {
                        bw = sz(this,'borderLeftWidth') + sz(this,'borderRightWidth');
                        ds.setExpression('width', 'this.parentNode.offsetWidth - '+bw+'+ "px"');
                    }
                    else
                        ds.width = '100%';
                }
                else {
                    ds.position = 'relative';
                    ds.margin = !bot ? '-'+pad.T+'px -'+pad.R+'px '+(pad.T-width)+'px -'+pad.L+'px' : 
                                        (pad.B-width)+'px -'+pad.R+'px -'+pad.B+'px -'+pad.L+'px';                
                }

                for (i=0; i < width; i++) {
                    w = Math.max(0,getWidth(fx,i, width));
                    e = strip.cloneNode(false);
                    e.style.borderWidth = '0 '+(opts[j+'R']?w:0)+'px 0 '+(opts[j+'L']?w:0)+'px';
                    bot ? d.appendChild(e) : d.insertBefore(e, d.firstChild);
                }
                
                if (fold && jQuery.support.boxModel) {
                    if (bot && noBottomFold) continue;
                    for (c in opts) {
                        if (!opts[c]) continue;
                        if (bot && (c == 'TL' || c == 'TR')) continue;
                        if (!bot && (c == 'BL' || c == 'BR')) continue;
                        
                        common = { position: 'absolute', border: 'none', margin: 0, padding: 0, overflow: 'hidden', backgroundColor: strip.style.borderColor };
                        jQueryhorz = jQuery('<div/>').css(common).css({ width: width + 'px', height: '1px' });
                        switch(c) {
                        case 'TL': jQueryhorz.css({ bottom: 0, left: 0 }); break;
                        case 'TR': jQueryhorz.css({ bottom: 0, right: 0 }); break;
                        case 'BL': jQueryhorz.css({ top: 0, left: 0 }); break;
                        case 'BR': jQueryhorz.css({ top: 0, right: 0 }); break;
                        }
                        d.appendChild(jQueryhorz[0]);
                        
                        var jQueryvert = jQuery('<div/>').css(common).css({ top: 0, bottom: 0, width: '1px', height: width + 'px' });
                        switch(c) {
                        case 'TL': jQueryvert.css({ left: width }); break;
                        case 'TR': jQueryvert.css({ right: width }); break;
                        case 'BL': jQueryvert.css({ left: width }); break;
                        case 'BR': jQueryvert.css({ right: width }); break;
                        }
                        d.appendChild(jQueryvert[0]);
                    }
                }
            }
        }
    });
};

jQuery.fn.uncorner = function() { 
    if (radius || moz || webkit)
        this.css(radius ? 'border-radius' : moz ? '-moz-border-radius' : '-webkit-border-radius', 0);
    jQuery('div.jquery-corner', this).remove();
    return this;
};

// expose options
jQuery.fn.corner.defaults = {
    useNative: true, // true if plugin should attempt to use native browser support for border radius rounding
    metaAttr:  'data-corner' // name of meta attribute to use for options
};
    
})(jQuery);
