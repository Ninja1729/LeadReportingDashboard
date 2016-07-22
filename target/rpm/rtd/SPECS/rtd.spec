%define __jar_repack 0
Name: rtd
Version: 1.0
Release: SNAPSHOT20160722211210
Summary: Lead Reporting Job
License: proprietary
Group: Truecar/BI
Requires: jdk >= 1.7.0
autoprov: yes
autoreq: yes
BuildArch: noarch
BuildRoot: /Users/nkandavel/Documents/workspace/LeadReportingDashboard/target/rpm/rtd/buildroot

%description

%install
if [ -d $RPM_BUILD_ROOT ];
then
  mv /Users/nkandavel/Documents/workspace/LeadReportingDashboard/target/rpm/rtd/tmp-buildroot/* $RPM_BUILD_ROOT
else
  mv /Users/nkandavel/Documents/workspace/LeadReportingDashboard/target/rpm/rtd/tmp-buildroot $RPM_BUILD_ROOT
fi

%files

%attr(755,nkandavel,nkandavel) "/home/nkandavel/testaws/oozie"
%attr(755,nkandavel,nkandavel)  "/home/nkandavel/testaws/lib/rtd-1.0-SNAPSHOT-with-deps.jar"

%post
# Substitute environmental values
                                    python /usr/bin/tplize.py /etc/template_values \
                                    /home/nkandavel/testaws/oozie/coordinator.properties



                                    chown -R nkandavel /home/nkandavel/testaws

                                    # Create project directory in HDFS
                                    echo "Checking for /user/nkandavel/testaws existence"
                                    sudo -u nkandavel hadoop fs -test -d /user/nkandavel/testaws
                                    if [ "$?" = "0" ]; then
                                    echo "Deleting existing workflow in /user/nkandavel/testaws"
                                    sudo -u nkandavel hadoop fs -rm -r -skipTrash /user/nkandavel/testaws
                                    fi
                                    sudo -u nkandavel hadoop fs -mkdir -p /user/nkandavel/testaws

                                    # Install oozie workflows
                                    echo "Installing oozie files"
                                    sudo -u nkandavel hadoop fs -copyFromLocal /home/nkandavel/testaws/oozie/* \
                                    /user/nkandavel/testaws

                                    # Install the jar
                                    echo "Installing uvleadsales jar"
                                    sudo -u nkandavel hadoop fs -mkdir -p /user/nkandavel/testaws/lib
                                    sudo -u nkandavel hadoop fs -copyFromLocal /home/nkandavel/testaws/lib/*1.0-SNAPSHOT-with-deps.jar \
                                    /user/nkandavel/testaws/lib

                                    echo "Installation completed successfully"
