//
//  DevicesViewController.m
//  Spela
//
//  Created by Daniel Thengvall on 8/23/14.
//  Copyright (c) 2014 Daniel Thengvall. All rights reserved.
//

#import "DevicesViewController.h"

@implementation DevicesViewController


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 2;
}



- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
        case 0:
            return [tableView dequeueReusableCellWithIdentifier:@"logoCell" forIndexPath:indexPath];
        case 1:
            return [tableView dequeueReusableCellWithIdentifier:@"deviceCell" forIndexPath:indexPath];
            //return [tableView dequeueReusableCellWithIdentifier:@"blank" forIndexPath:indexPath];
        
    }
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"blank" forIndexPath:indexPath];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return indexPath.row == 0 ? 80 : 60;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row > 0) {
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

@end
