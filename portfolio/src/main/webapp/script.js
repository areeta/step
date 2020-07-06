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


/** Adds scroll bar to show progress of page scroll.  */
window.onscroll = function() {handleOnScroll()};

function handleOnScroll() {
  var winScroll = document.body.scrollTop || document.documentElement.scrollTop;
  var height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
  var scrolled = (winScroll / height) * 100;
  document.getElementById("myBar").style.width = scrolled + "%";
}

/** Fetches posts from the server and adds them to the DOM. */
function getPosts() {
  fetch('/list-posts').then(response => response.json()).then((posts) => {
    const postListElement = document.getElementById('posts');
    posts.forEach((post) => {
      postListElement.appendChild(createPostElement(post));
    })
  });
}

/** Creates an element that represents a post. */
function createPostElement(post) {
  
  const postElement = document.createElement('div');
  postElement.setAttribute("class", "post");

  // Add user contact information to post.
  const contactElement = document.createElement('h5');
  contactElement.innerText = post.firstName + " " + post.lastName + " • " + post.email;

  // Add user message to post.
  const messageElement = document.createElement('p');
  messageElement.innerText = post.message;

  postElement.appendChild(contactElement);
  postElement.appendChild(messageElement);
  return postElement;
}
