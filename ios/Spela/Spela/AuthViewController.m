//
//  AuthViewController.m
//  Spela
//
//  Created by Daniel Thengvall on 8/21/14.
//  Copyright (c) 2014 Daniel Thengvall. All rights reserved.
//

#import "AuthViewController.h"

@implementation AuthViewController

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 4;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
        case 0:
            return [tableView dequeueReusableCellWithIdentifier:@"logoCell" forIndexPath:indexPath];

        case 1:
            return [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
        case 2:
            return [tableView dequeueReusableCellWithIdentifier:@"initialCell" forIndexPath:indexPath];
        case 3:
            return [tableView dequeueReusableCellWithIdentifier:@"playCell" forIndexPath:indexPath];
    }
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return indexPath.row == 0 ? 80 : 60;
}

@end
