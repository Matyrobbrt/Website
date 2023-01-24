import '@vaadin/icon/vaadin-iconset.js';

const template = document.createElement('template');

template.innerHTML = `<vaadin-iconset name="custom-icons" size="32">
  <svg><defs>
    <g id="custom-icons:curseforge"><svg xmlns="http://www.w3.org/2000/svg"  viewBox="0 0 50 50" width="32px" height="32px"><path d="M 14.015625 16 L 14.640625 18 L 0 18 L 0 19 C 0 23.598 7.9684844 27.372516 18.271484 27.728516 L 20.269531 34 L 21.964844 34 L 19.892578 41.619141 L 37.539062 41.619141 L 36.019531 34 L 37.728516 34 L 37.951172 33.306641 C 39.727172 27.786641 44.366469 23.127609 49.230469 21.974609 L 50 21.791016 L 50 16 L 14.015625 16 z M 16.734375 18 L 48 18 L 48 20.234375 C 42.985 21.757375 38.319156 26.437 36.285156 32 L 34.382812 32 L 32.882812 35 L 34.179688 35 L 35.101562 39.619141 L 22.509766 39.619141 L 23.763672 35 L 24.818359 35 L 23.693359 32 L 21.730469 32 L 19.742188 25.761719 L 19.025391 25.748047 C 10.079391 25.579047 3.5502344 22.763 2.2402344 20 L 17.359375 20 L 16.734375 18 z M 30.65625 19.5 C 30.65625 19.5 28.673938 20.339625 28.210938 21.265625 C 27.667937 22.352625 28.346422 23.303656 28.482422 23.847656 C 28.618422 24.391656 27.531766 24.119688 27.259766 23.304688 C 26.988766 24.391687 27.124969 24.52875 27.667969 25.34375 C 28.241969 26.20475 28.397797 28.060547 26.716797 28.060547 C 24.950797 28.060547 25.630859 25.75 25.630859 25.75 C 25.630859 25.75 24 26.564828 24 27.923828 C 24 30.369828 26.174719 30.913344 27.261719 30.777344 C 28.348719 30.641344 29.162109 32 29.162109 32 C 29.298109 31.321 29.570281 31.184594 30.113281 31.183594 C 30.721281 31.183594 31.608109 30.912656 32.287109 30.097656 C 33.580109 28.545656 33.238312 27.379453 32.695312 26.564453 C 32.967312 28.874453 31.472687 29.009766 30.929688 29.009766 C 30.197688 29.009766 29.435203 27.652141 30.658203 27.244141 C 31.880203 26.836141 31.337891 25.884766 31.337891 25.884766 C 31.337891 25.884766 30.928281 26.69925 30.113281 26.15625 C 29.313281 25.62425 30.792969 24.527344 30.792969 24.527344 C 30.792969 24.527344 29.434828 23.305547 29.298828 21.810547 C 29.162828 20.315547 30.65625 19.5 30.65625 19.5 z"/></svg></g>
    <g id="custom-icons:discord"><?xml version="1.0"?><svg xmlns="http://www.w3.org/2000/svg"  viewBox="0 0 30 30" width="32px" height="32px">    <path d="M25.12,6.946c-2.424-1.948-6.257-2.278-6.419-2.292c-0.256-0.022-0.499,0.123-0.604,0.357 c-0.004,0.008-0.218,0.629-0.425,1.228c2.817,0.493,4.731,1.587,4.833,1.647c0.478,0.278,0.638,0.891,0.359,1.368 C22.679,9.572,22.344,9.75,22,9.75c-0.171,0-0.343-0.043-0.501-0.135C21.471,9.598,18.663,8,15.002,8 C11.34,8,8.531,9.599,8.503,9.615C8.026,9.892,7.414,9.729,7.137,9.251C6.86,8.775,7.021,8.164,7.497,7.886 c0.102-0.06,2.023-1.158,4.848-1.65c-0.218-0.606-0.438-1.217-0.442-1.225c-0.105-0.235-0.348-0.383-0.604-0.357 c-0.162,0.013-3.995,0.343-6.451,2.318C3.564,8.158,1,15.092,1,21.087c0,0.106,0.027,0.209,0.08,0.301 c1.771,3.11,6.599,3.924,7.699,3.959c0.007,0.001,0.013,0.001,0.019,0.001c0.194,0,0.377-0.093,0.492-0.25l1.19-1.612 c-2.61-0.629-3.99-1.618-4.073-1.679c-0.444-0.327-0.54-0.953-0.213-1.398c0.326-0.443,0.95-0.541,1.394-0.216 C7.625,20.217,10.172,22,15,22c4.847,0,7.387-1.79,7.412-1.808c0.444-0.322,1.07-0.225,1.395,0.221 c0.324,0.444,0.23,1.066-0.212,1.392c-0.083,0.061-1.456,1.048-4.06,1.677l1.175,1.615c0.115,0.158,0.298,0.25,0.492,0.25 c0.007,0,0.013,0,0.019-0.001c1.101-0.035,5.929-0.849,7.699-3.959c0.053-0.092,0.08-0.195,0.08-0.301 C29,15.092,26.436,8.158,25.12,6.946z M11,19c-1.105,0-2-1.119-2-2.5S9.895,14,11,14s2,1.119,2,2.5S12.105,19,11,19z M19,19 c-1.105,0-2-1.119-2-2.5s0.895-2.5,2-2.5s2,1.119,2,2.5S20.105,19,19,19z"/></svg></g>
    <g id="custom-icons:github"><svg xmlns="http://www.w3.org/2000/svg"  viewBox="0 0 32 32" width="32px" height="32px" fill-rule="evenodd"><path fill-rule="evenodd" d="M 16 4 C 9.371094 4 4 9.371094 4 16 C 4 21.300781 7.4375 25.800781 12.207031 27.386719 C 12.808594 27.496094 13.027344 27.128906 13.027344 26.808594 C 13.027344 26.523438 13.015625 25.769531 13.011719 24.769531 C 9.671875 25.492188 8.96875 23.160156 8.96875 23.160156 C 8.421875 21.773438 7.636719 21.402344 7.636719 21.402344 C 6.546875 20.660156 7.71875 20.675781 7.71875 20.675781 C 8.921875 20.761719 9.554688 21.910156 9.554688 21.910156 C 10.625 23.746094 12.363281 23.214844 13.046875 22.910156 C 13.15625 22.132813 13.46875 21.605469 13.808594 21.304688 C 11.144531 21.003906 8.34375 19.972656 8.34375 15.375 C 8.34375 14.0625 8.8125 12.992188 9.578125 12.152344 C 9.457031 11.851563 9.042969 10.628906 9.695313 8.976563 C 9.695313 8.976563 10.703125 8.65625 12.996094 10.207031 C 13.953125 9.941406 14.980469 9.808594 16 9.804688 C 17.019531 9.808594 18.046875 9.941406 19.003906 10.207031 C 21.296875 8.65625 22.300781 8.976563 22.300781 8.976563 C 22.957031 10.628906 22.546875 11.851563 22.421875 12.152344 C 23.191406 12.992188 23.652344 14.0625 23.652344 15.375 C 23.652344 19.984375 20.847656 20.996094 18.175781 21.296875 C 18.605469 21.664063 18.988281 22.398438 18.988281 23.515625 C 18.988281 25.121094 18.976563 26.414063 18.976563 26.808594 C 18.976563 27.128906 19.191406 27.503906 19.800781 27.386719 C 24.566406 25.796875 28 21.300781 28 16 C 28 9.371094 22.628906 4 16 4 Z"/></svg></g>
  </defs></svg>
</vaadin-iconset>`;

document.head.appendChild(template.content);