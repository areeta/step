// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


/**
 * Adds scroll bar to show progress of page scroll.
 */
window.onscroll = function() {handleOnScroll()};

function handleOnScroll() {
  var winScroll = document.body.scrollTop || document.documentElement.scrollTop;
  var height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
  var scrolled = (winScroll / height) * 100;
  document.getElementById("myBar").style.width = scrolled + "%";
}

/**
 * Adds posts.
 */
function getPosts() {
  fetch('/posts').then(response => response.json()).then((posts) => {

    // Include data of one Post.
    if (posts.length > 0) {
      const post = document.getElementById('posts-container');
      post.innerText = "first name: " + posts[0]["firstName"];
      post.innerText += "last name: " + posts[0]["lastName"];
      post.innerText += "email: " + posts[0]["email"];
      post.innerText += "message: " + posts[0]["message"];
    }
  });
}
