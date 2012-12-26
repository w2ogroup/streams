package path;

# Single-level navbar. Entries without an href are headers.
our @nav = (
    { title => "Apache Streams" },
    { title => "Overview",
      href => "/index.html"},
    { title => "Wiki (coming soon)",
      href => ""},
    { title => "Project" },
     { title => "Architecture",
      href => "/architecture.html" },
    { title => "Source Code",
      href => "/source-repository.html" },
    { title => "Mailing Lists",
      href => "/mailing-lists.html" },
    { title => "People",
      href => "/people.html" },
    { title => "Issue Tracking",
      href => "https://issues.apache.org/jira/browse/STREAMS" },
    { title => "Release Management",
      href => "/release-management.html" },
    { title => "Resources",
      href => "/resources.html" },
    { title => "Foundation" },
    { title => "Foundation Info",
      href => "http://www.apache.org/" },
    { title => "License",
      href => "http://apache.org/licenses/LICENSE-2.0.html" },
    { title => "Sponsorship",
      href => "http://www.apache.org/foundation/sponsorship.html" },
    { title => "Thanks",
      href => "http://www.apache.org/foundation/thanks.html" },
);

# Django-style URL dispatching
our @patterns = (
  [qr!\.mdtext$!, single_narrative => { 
    template => "skeleton.html", 
    nav => \@nav }],
);

1;

=head1 LICENSE

           Licensed to the Apache Software Foundation (ASF) under one
           or more contributor license agreements.  See the NOTICE file
           distributed with this work for additional information
           regarding copyright ownership.  The ASF licenses this file
           to you under the Apache License, Version 2.0 (the
           "License"); you may not use this file except in compliance
           with the License.  You may obtain a copy of the License at

             http://www.apache.org/licenses/LICENSE-2.0

           Unless required by applicable law or agreed to in writing,
           software distributed under the License is distributed on an
           "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
           KIND, either express or implied.  See the License for the
           specific language governing permissions and limitations
           under the License.

