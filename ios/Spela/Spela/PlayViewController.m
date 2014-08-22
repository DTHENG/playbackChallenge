//
//  PlayViewController.m
//  Spela
//
//  Created by Daniel Thengvall on 8/21/14.
//  Copyright (c) 2014 Daniel Thengvall. All rights reserved.
//

#import "PlayViewController.h"

@implementation PlayViewController

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 6;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
        case 0:
            return [tableView dequeueReusableCellWithIdentifier:@"logoCell" forIndexPath:indexPath];
        case 1:
        case 4:
            return [tableView dequeueReusableCellWithIdentifier:@"blank" forIndexPath:indexPath];
        case 2:
            return [tableView dequeueReusableCellWithIdentifier:@"songTitleCell" forIndexPath:indexPath];
        case 3:
            return [tableView dequeueReusableCellWithIdentifier:@"artistCell" forIndexPath:indexPath];
        case 5: {
            
            
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"controls" forIndexPath:indexPath];
            
            [cell addSubview:[self getControlImage:@"previous-active" :40]];
            [cell addSubview:[self getControlImage:@"play-active" :100]];

            [cell addSubview:[self getControlImage:@"pause-active" :160]];
            [cell addSubview:[self getControlImage:@"next-active" :220]];

            return cell;

        }


    }
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"blank" forIndexPath:indexPath];
    return cell;
}

- (UIImageView *)getControlImage:(NSString *)filename :(CGFloat)left {
    NSURL *url = [NSURL fileURLWithPath:[NSBundle.mainBundle pathForResource:filename ofType:@"pdf"]];
    CGSize size = CGSizeMake(308, 308); // Output image size
    CGPDFDocumentRef document = CGPDFDocumentCreateWithURL((CFURLRef)url);
    UIGraphicsBeginImageContextWithOptions(size, false, 0);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextTranslateCTM(context, 0.0, size.height);
    CGContextScaleCTM(context, 1.0, -1.0);
    CGPDFPageRef page = CGPDFDocumentGetPage(document, 1);
    CGContextConcatCTM(context, CGPDFPageGetDrawingTransform(page, kCGPDFBleedBox, CGRectMake(0, 0, size.width, size.height), 0, true));
    CGContextDrawPDFPage(context, page);
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    
    UIImageView *imageview = [[UIImageView alloc] initWithImage: image];
    
    [imageview setFrame:CGRectMake(left, 0, 60, 60)];
    //[imageview setBackgroundColor:[UIColor greenColor]];

    return imageview;

}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return indexPath.row == 0 ? 80 : 60;
}

@end
